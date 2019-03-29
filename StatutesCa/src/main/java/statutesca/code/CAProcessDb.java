package statutesca.code;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import statutes.StatuteRange;
import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;
import statutes.StatutesNode;
import statutes.StatutesRoot;
import statutes.api.IStatutesApi;
import statutesca.statutesapi.CAStatutesApiImpl;


public class CAProcessDb {

	protected final QueryRunner queryRunner;
	protected static final String url = "jdbc:mysql://localhost:3306/capublic?autoReconnect=true&useSSL=false";
	protected static final String driver = "com.mysql.jdbc.Driver";
	protected static final String usr = "root";
	protected static final String pwd = "Fr0m5n@w";
	protected final Connection conn;
	protected final ResultSetHandler<List<LawForCodeSections>> lawForCodeHandler;
	protected final ResultSetHandler<List<LawCode>> lawCodeHandler;
	private List<LawSection> lawSections;
	private static final String DIVISION = "DIVISION";
	private static final int DIVISION_LEN = DIVISION.length();
	private static final String PART = "PART";
	private static final int PART_LEN = PART.length();
	private static final String TITLE = "TITLE";
	private static final int TITLE_LEN = TITLE.length();
	private static final String CHAPTER = "CHAPTER";
	private static final int CHAPTER_LEN = CHAPTER.length();
	private static final String ARTICLE = "ARTICLE";
	private static final int ARTICLE_LEN = ARTICLE.length();
	private final Pattern pattern = Pattern.compile("\\[(.*?)\\]$");
	protected int position;


	public CAProcessDb() throws SQLException {
		queryRunner = new QueryRunner();
		DbUtils.loadDriver(driver);
		conn = DriverManager.getConnection(url, usr, pwd);
		position = 1;
		lawCodeHandler = new BeanListHandler<LawCode>(LawCode.class, new BasicRowProcessor(new GenerousBeanProcessor()));
//		lawForCodeHandler = new BeanListHandler<LawForCode>(LawForCode.class, new BasicRowProcessor(new GenerousBeanProcessor()));
		lawForCodeHandler = rs-> {
			List<LawForCodeSections> beanList = new ArrayList<>();
			BeanProcessor bp = new GenerousBeanProcessor();
			LawForCodeSections previous = null;
			while ( rs.next() ) {
				LawForCode lawForCode = bp.populateBean(rs, new LawForCode());
				if ( !beanList.isEmpty() && previous.getNode_treepath() != null && previous.getNode_treepath().equals(lawForCode.getNode_treepath()) ) {
					previous.getSections().add(new LawSection(lawForCode.getSection_num(), lawForCode.getContent_xml()));
				} else {
					LawForCodeSections codeSections = new LawForCodeSections(lawForCode);
					beanList.add(codeSections);
					previous = codeSections;
				}
			}
			return beanList;
		};
	}

	public List<LawCode> retrieveLawCodes() throws SQLException  {
		return queryRunner.query(conn, "select * from codes_tbl", lawCodeHandler);
	}

	public  List<LawForCodeSections> retrieveLawForCode(String lawCode) throws SQLException  {
		return queryRunner.query(conn,
			"select l.division,l.title,l.part,l.chapter,l.article,l.heading,l.active_flg,l.node_level,l.node_position,l.node_treepath,l.contains_law_sections,s.section_num,s.content_xml from law_toc_tbl l left outer join law_section_tbl s on s.id in " + 
			"(select id from law_toc_sections_tbl where law_code = l.law_code and node_treepath = l.node_treepath order by section_order)" + 
			"where l.law_code = ? and l.active_flg = 'Y'" + 
			"order by l.node_sequence", lawForCodeHandler, lawCode);
	}

	
	public StatutesRoot parseLawCode(String lawCode, BiConsumer<LawForCodeSections, StatutesLeaf> leafConsumer) throws SQLException {
		List<LawForCodeSections> lawForCodes = retrieveLawForCode(lawCode);
		// purposely don't call loadStatutes because we are getting them from the raw
		// files
		IStatutesApi iStatutesApi = new CAStatutesApiImpl();
	
		Stack<StatutesBaseClass> secStack = new Stack<>();
		StatutesRoot statutesRoot = null;
		boolean foundCode = false;
		
		for (LawForCodeSections lawForCode: lawForCodes) {
			if ( lawForCode.getHeading().toLowerCase().contains("title of act")) {
				continue;
			}
			if ( ! foundCode ) {
				statutesRoot = new StatutesRoot(
						lawCode, 
						iStatutesApi.getFullTitle(lawCode), 
						iStatutesApi.getShortTitle(lawCode), 
						lawCode + "-0");
				secStack.push(statutesRoot);
				foundCode = true;
				continue;
			}
			if (secStack.size() > (lawForCode.getNode_level())) {
				while (secStack.size() > (lawForCode.getNode_level())) {
					secStack.pop();
				}
			}
			String heading = lawForCode.getHeading().toUpperCase();
			String part = "";
			String partNumber = "";
			if (heading.substring(0, DIVISION_LEN).equals(DIVISION)) {
				part = DIVISION;
				partNumber = lawForCode.getDivision() == null ? "" : lawForCode.getDivision().toUpperCase();
				heading = heading.replace(DIVISION + " " + partNumber, "").trim();
			}
			if (heading.substring(0, PART_LEN).equals(PART)) {
				part = PART;
				partNumber = lawForCode.getPart() == null ? "" : lawForCode.getPart().toUpperCase();
				heading = heading.replace(PART + " " + partNumber, "").trim();
			}
			if (heading.substring(0, TITLE_LEN).equals(TITLE)) {
				part = TITLE;
				partNumber = lawForCode.getTitle() == null ? "" : lawForCode.getTitle().toUpperCase();
				heading = heading.replace(TITLE + " " + partNumber, "").trim();
			}
			if (heading.substring(0, CHAPTER_LEN).equals(CHAPTER)) {
				part = CHAPTER;
				partNumber = lawForCode.getChapter() == null ? "" : lawForCode.getChapter().toUpperCase();
				heading = heading.replace(CHAPTER + " " + partNumber, "").trim();
			}
			if (heading.substring(0, ARTICLE_LEN).equals(ARTICLE)) {
				part = ARTICLE;
				partNumber = lawForCode.getArticle() == null ? "" : lawForCode.getArticle().toUpperCase();
				heading = heading.replace(ARTICLE + " " + partNumber, "").trim();
			}
			StatutesBaseClass parent = secStack.peek();
			String fullFacet = parent.getFullFacet() + "/" + lawCode + "-" + lawForCode.getNode_level() + "-" + lawForCode.getNode_position();
			StatutesBaseClass statutesBaseClass;
			if (lawForCode.getContains_law_sections().equalsIgnoreCase("N")) {
				statutesBaseClass = new StatutesNode(
						parent, 
						fullFacet, 
						part, 
						partNumber, 
						heading, 
						lawForCode.getNode_level()
					);
				if (secStack.size() < (lawForCode.getNode_level() + 1)) {
					secStack.push(statutesBaseClass);
				}
			} else {
				StatutesLeaf statutesLeaf = new StatutesLeaf(
						parent, 
						fullFacet, 
						part,  
						partNumber, 
						heading, 
						lawForCode.getNode_level(), 
						new StatuteRange()
					);
				leafConsumer.accept(lawForCode, statutesLeaf);
				statutesBaseClass = statutesLeaf;
						
			}

			parent.addReference(statutesBaseClass);
			position++;
		}
		trimHeadings(statutesRoot);
		return statutesRoot;
	}

	private void trimHeadings(StatutesRoot statutesRoot) {
		Stack<StatutesBaseClass> stack = new Stack<>();
		stack.push(statutesRoot);
		// 
		while (!stack.isEmpty()) {
			// process this one then push its references
			StatutesBaseClass currentPos = stack.pop();
			currentPos.setTitle( pattern.matcher(currentPos.getTitle()).replaceAll("").trim() );
			if ( currentPos.getReferences() != null  ) {
				for ( StatutesBaseClass currentRef: currentPos.getReferences() ) {
					stack.push(currentRef);
				}
			}
		}
	}

	public List<LawSection> getLawSections() {
		return lawSections;
	}

}
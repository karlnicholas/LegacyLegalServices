package statutesca.code;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;

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

	public CAProcessDb() throws SQLException {
		queryRunner = new QueryRunner();
		DbUtils.loadDriver(driver);
		conn = DriverManager.getConnection(url, usr, pwd);
		lawCodeHandler = new BeanListHandler<LawCode>(LawCode.class, new BasicRowProcessor(new GenerousBeanProcessor()));
//		lawForCodeHandler = new BeanListHandler<LawForCode>(LawForCode.class, new BasicRowProcessor(new GenerousBeanProcessor()));
		lawForCodeHandler = rs-> {
			List<LawForCodeSections> beanList = new ArrayList<>();
			BeanProcessor bp = new GenerousBeanProcessor();
			LawForCodeSections previous = null;
			while ( rs.next() ) {
				LawForCode lawForCode = bp.populateBean(rs, new LawForCode());
				if ( !beanList.isEmpty() ) {
					if ( previous.getNode_treepath() != null && previous.getNode_treepath().equals(lawForCode.getNode_treepath()) ) {
						previous.getSections().add(new LawSection(lawForCode.getSection_num(), lawForCode.getContent_xml()));
					} else {
						LawForCodeSections codeSections = new LawForCodeSections(lawForCode);
						beanList.add(codeSections);
						previous = codeSections;
					}
				} else {
					LawForCodeSections codeSections = new LawForCodeSections(lawForCode);
					beanList.add(codeSections);
					previous = codeSections;
				}
			}
			return beanList;
		};
	}
/*
	public List<LawCode> retrieveLawCodes() throws SQLException  {
		return queryRunner.query(conn, "select * from codes_tbl", lawCodeHandler);
	}
*/
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
	/*
	select l.*, s.* from law_toc_tbl l left outer join law_section_tbl s on s.id = 
	(select id from law_toc_sections_tbl where law_code = l.law_code and node_treepath = l.node_treepath and section_order = (select max(section_order) from law_toc_sections_tbl where law_code = l.law_code and node_treepath = l.node_treepath))
	where l.law_code = 'CCP' and l.active_flg = 'Y' 
	order by l.node_sequence

	select l.law_code,l.division,l.title,l.part,l.chapter,l.article,l.heading,l.active_flg,l.node_level,l.node_position,l.contains_law_sections,s.section_num,s.content_xml from law_toc_tbl l left outer join law_section_tbl s on s.id = 
	(select id from law_toc_sections_tbl where law_code = l.law_code and node_treepath = l.node_treepath and section_order = (select max(section_order) from law_toc_sections_tbl where law_code = l.law_code and node_treepath = l.node_treepath))
	where l.law_code = 'CCP' and l.active_flg = 'Y' 
	order by l.node_sequence

	select l.*,s.* from law_toc_tbl l left outer join law_section_tbl s on s.id in 
	(select id from law_toc_sections_tbl where law_code = l.law_code and node_treepath = l.node_treepath)
	where l.law_code = 'CCP' and l.active_flg = 'Y' 
	order by l.node_sequence;


	*/

	
	public StatutesRoot parseLawCode(String lawCode, BiConsumer<LawForCodeSections, StatutesLeaf> leafConsumer) throws SQLException {
		List<LawForCodeSections> lawForCodes = retrieveLawForCode(lawCode);;
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
			String part = iStatutesApi.getShortTitle(lawCode);
			String partNumber = "";
			if (lawForCode.getTitle() != null) {
				part = "Title";
				partNumber = lawForCode.getTitle(); 
			}	
			if (lawForCode.getPart() != null) {
				part = "Part";
				partNumber = lawForCode.getPart(); 
			}
			if (lawForCode.getDivision() != null) {
				part = "Division";
				partNumber = lawForCode.getDivision(); 
			}
			if (lawForCode.getChapter() != null) {
				part = "Chapter";
				partNumber = lawForCode.getChapter(); 
			}
			if (lawForCode.getArticle() != null) {
				part = "Article";
				partNumber = lawForCode.getArticle(); 
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
						lawForCode.getHeading(), 
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
						lawForCode.getHeading(), 
						lawForCode.getNode_level(), 
						new StatuteRange()
					);
				leafConsumer.accept(lawForCode, statutesLeaf);
				statutesBaseClass = statutesLeaf;
						
			}
			statutesBaseClass.setTitle(statutesBaseClass.getTitle().replace(statutesBaseClass.getPart().toUpperCase() + " " + statutesBaseClass.getPartNumber(), "").trim());

			parent.addReference(statutesBaseClass);
		}
		return statutesRoot;
	}

	public List<LawSection> getLawSections() {
		return lawSections;
	}

}
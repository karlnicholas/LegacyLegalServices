package statutes;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Test;

public class StatutesTest {

	@Test
	public void test() {
		StatutesRoot statutesRoot = new StatutesRoot();
		assertNotNull("StatutesTitles not created", statutesRoot);
		statutesRoot.setParent( null );
		statutesRoot.setTitle("title");
		statutesRoot.setShortTitle("shortTitle");
		statutesRoot.setFullFacet("fullFacet");
		statutesRoot.setDepth(0);
		statutesRoot.setPart("part");
		statutesRoot.setPartNumber("partNumber");
		
		SectionNumber sectionNumber1 = new SectionNumber(1, "1");
		SectionNumber sectionNumber2 = new SectionNumber(2, "2");
		SectionNumber sectionNumber3 = new SectionNumber(3, "3");
		SectionNumber sectionNumber4 = new SectionNumber(4, "4");

		StatuteRange statuteRange = new StatuteRange(sectionNumber1,  sectionNumber4);
		statutesRoot.setStatuteRange( statuteRange );
		
		// create leafs
		StatutesLeaf statutesLeaf1 = new StatutesLeaf(
				null, 
				"fullFacet", 
				"part", 
				"partNumber", 
				"title", 
				2, 
				statuteRange
			);
		ArrayList<SectionNumber> sectionNumbers = new ArrayList<>();
		sectionNumbers.add(sectionNumber1);
		sectionNumbers.add(sectionNumber4);		
		statutesLeaf1.setSectionNumbers(sectionNumbers);

		StatuteRange statuteRange2 = new StatuteRange(sectionNumber2,  sectionNumber3);
		StatutesLeaf statutesLeaf2 = new StatutesLeaf(
				null, 
				"fullFacet", 
				"part", 
				"partNumber", 
				"title", 
				3, 
				statuteRange2
			);
		sectionNumbers = new ArrayList<>();
		sectionNumbers.add(sectionNumber2);
		sectionNumbers.add(sectionNumber3);		
		statutesLeaf2.setSectionNumbers(sectionNumbers);
		statutesLeaf1.addReference(statutesLeaf2);
		
		// create node
		StatutesNode statutesNode = new StatutesNode();
		statutesNode.setParent(statutesRoot);
		assertEquals( statutesRoot, statutesNode.getParent() );
		assertEquals("title-", statutesNode.getFullTitle("-"));
		assertNull( statutesNode.getTitle() );
		assertEquals( "", statutesNode.getTitle(false) );
		assertEquals( "", statutesNode.getTitle(true) );
		assertEquals( "", statutesNode.getShortTitle() );
		statutesNode.setShortTitle("shortTitle");
		statutesNode.setTitle("title");
		assertEquals("title-title", statutesNode.getFullTitle("-"));
		
		statutesNode.setFullFacet("fullFacet");
		statutesNode.setDepth(1);
		statutesNode.setPart("part");
		assertEquals("title-title", statutesNode.getFullTitle("-"));
		assertEquals( "title", statutesNode.getTitle(false) );
		assertEquals( "title", statutesNode.getTitle(true) );
		assertEquals( "part ", statutesNode.getShortTitle() );
		statutesNode.setPartNumber("partNumber");
		assertEquals("title-part partNumber. title", statutesNode.getFullTitle("-"));
		assertEquals( "title", statutesNode.getTitle(false) );
		assertEquals( "part partNumber. title", statutesNode.getTitle(true) );
		assertEquals( "part partNumber", statutesNode.getShortTitle() );
		statutesNode.addReference(statutesLeaf1);
		assertEquals( 1, statutesNode.getReferences().size() );
		assertEquals( "title-part partNumber. title", statutesNode.getFullTitle("-") );

		statutesNode.setStatuteRange( statuteRange );

		statutesRoot.addReference(statutesNode);

		statutesRoot.rebuildParentReferences(null);
		
		ArrayList<StatutesBaseClass> parents = new ArrayList<>(); 
		testLeaf(statutesLeaf1);
		statutesLeaf1.getParents(parents);
		assertEquals(2, parents.size());
		parents.clear();
		statutesNode.getParents(parents);
		assertEquals(1, parents.size());

		assertEquals( 1, statutesRoot.getReferences().size() );
		assertEquals( "title: 1 references", statutesRoot.toString() );
		statutesRoot.getParents(null);	// test no exception

		testNode(statutesNode);
		testRootBase(statutesRoot);
		testRootParts(statutesRoot);

		assertEquals( statutesRoot, statutesLeaf1.getStatutesRoot() );

		assertNotNull( statutesRoot.findReference(sectionNumber4) );
		assertNotNull( statutesRoot.findReference(sectionNumber1) );
		assertNull( statutesRoot.findReference(new SectionNumber()) );

		// test iterator handler
		try {
			statutesRoot.iterateLeafs( new IteratorHandler() {
				@Override
				public boolean handleSection(StatutesLeaf section) throws Exception {
					return section.getDepth() != 4;
				} 
			});
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		statutesRoot = new StatutesRoot( "lawCode", "title", "shortTitle", "fullFacet" );
		assertNotNull("StatutesTitles not created", statutesRoot);
		assertEquals( 0, statutesRoot.getReferences().size() );
		testRootBase(statutesRoot);
		statutesRoot.addReference(statutesLeaf1);
		// test iterator handler
		try {
			statutesRoot.iterateLeafs( new IteratorHandler() {
				@Override
				public boolean handleSection(StatutesLeaf section) throws Exception {
					return section.getDepth() == 0;
				} 
			});
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		// test iterator handler
		try {
			statutesNode.iterateLeafs( new IteratorHandler() {
				@Override
				public boolean handleSection(StatutesLeaf section) throws Exception {
					return section.getDepth() == 1;
				} 
			});
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		statutesNode = new StatutesNode(statutesRoot, "fullFacet", "part", "partNumber", "title", 1);
		testNode2(statutesNode);
		assertEquals( "title-part partNumber. title", statutesNode.getFullTitle("-") );
		assertEquals( "part partNumber:title", statutesNode.toString() );		
		assertNotNull( statutesNode.getPart() );
		assertNotNull( statutesNode.getPartNumber() );

		statutesNode = new StatutesNode(statutesRoot, "fullFacet", null, null, "title", 1);
		testNode2(statutesNode);
		assertEquals( "title-title", statutesNode.getFullTitle("-") );
		assertEquals( "null null:title", statutesNode.toString() );
		assertNull( statutesNode.getPart() );
		assertNull( statutesNode.getPartNumber() );
	}
	
	private void testRootBase(StatutesRoot statutesRoot) {
		
	    assertNull( statutesRoot.getParent()  );
		assertEquals( "title", statutesRoot.getTitle() );
		assertEquals( "title", statutesRoot.getTitle(true) );
		assertEquals( "title", statutesRoot.getFullTitle("-") );
		assertEquals( "shortTitle", statutesRoot.getShortTitle() );
		assertEquals( "fullFacet", statutesRoot.getFullFacet() );
		assertEquals( 0, statutesRoot.getDepth() );
		assertNull( statutesRoot.getStatutesLeaf() );
		assertNull( statutesRoot.getStatutesNode() );
		
		assertEquals( true, statutesRoot.equals(statutesRoot) );
		assertEquals( false, statutesRoot.equals(null) );
		StatutesRoot tRoot = new StatutesRoot();
		StatutesRoot tRoot2 = new StatutesRoot();
		assertEquals( false, statutesRoot.equals(tRoot) );
		assertEquals( false, tRoot.equals(statutesRoot) );
		assertEquals( true, tRoot.equals(tRoot2) );
		assertEquals( 31 + 0,  tRoot.hashCode() );
		tRoot.setShortTitle("shortTitle");
		assertEquals( true, statutesRoot.equals(tRoot) );
		assertEquals( true, tRoot.equals(statutesRoot) );
		assertEquals( false, statutesRoot.equals(new Object()) );
		assertEquals( 31 + "shortTitle".hashCode(), statutesRoot.hashCode() );
	}
	
	private void testRootParts(StatutesRoot statutesRoot) {
		assertEquals( 1, statutesRoot.getStatuteRange().getsNumber().getPosition() );
		assertEquals( 4, statutesRoot.getStatuteRange().geteNumber().getPosition() );
		assertEquals( statutesRoot, statutesRoot.getStatutesRoot() );
		assertEquals( 0, statutesRoot.compareTo(statutesRoot) );
		assertNull( statutesRoot.getPart() );
		assertNull( statutesRoot.getPartNumber() );

	}
	
	private void testNode(StatutesNode statutesNode) {
		assertEquals( "fullFacet", statutesNode.getFullFacet() );
		assertEquals( 1, statutesNode.getDepth() );
		assertNull( statutesNode.getStatutesLeaf() );
		assertEquals( statutesNode, statutesNode.getStatutesNode() );
		
		assertEquals( 1, statutesNode.getStatuteRange().getsNumber().getPosition() );
		assertEquals( 4, statutesNode.getStatuteRange().geteNumber().getPosition() );
		assertNull( statutesNode.getStatutesLeaf() );
		assertEquals( "part partNumber:title", statutesNode.toString() );
		assertNotNull( statutesNode.getPart() );
		assertNotNull( statutesNode.getPartNumber() );

	}

	private void testNode2(StatutesNode statutesNode) {
		assertEquals( "fullFacet", statutesNode.getFullFacet() );
		assertEquals( 1, statutesNode.getDepth() );
		assertNull( statutesNode.getStatutesLeaf() );
		assertEquals( statutesNode, statutesNode.getStatutesNode() );

	}

	private void testLeaf(StatutesLeaf statutesLeaf) {
		assertEquals( "title", statutesLeaf.getTitle() );
		assertEquals( "title", statutesLeaf.getTitle(false) );
		assertEquals( "part partNumber. title", statutesLeaf.getTitle(true) );
		assertEquals( "title-part partNumber. title-part partNumber. title", statutesLeaf.getFullTitle("-") );
		assertEquals( "part partNumber", statutesLeaf.getShortTitle() );
		assertEquals( "fullFacet", statutesLeaf.getFullFacet() );
		assertEquals( 1, statutesLeaf.getStatuteRange().getsNumber().getPosition() );
		assertEquals( 4, statutesLeaf.getStatuteRange().geteNumber().getPosition() );
		assertNotNull( statutesLeaf.getPart() );
		assertNotNull( statutesLeaf.getPartNumber() );
		assertEquals( 2, statutesLeaf.getDepth() );
		assertNotNull( statutesLeaf.getStatutesLeaf() );
		assertNull( statutesLeaf.getStatutesNode() );

	}

}

package statutes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class StatutesTitlesTest {

	@Test
	public void test() {
		StatutesTitles statutesTitles = new StatutesTitles();
		assertNotNull("StatutesTitles not created", statutesTitles);
		statutesTitles.setLawCode("bpc");
		statutesTitles.setShortTitle("Bus. & Professions");
		statutesTitles.setCommonTitle("business and professions code");
		statutesTitles.setFullTitle("california business and professions code");
		statutesTitles.setAbvrTitles( new String[]{"bus. & prof. code"} );
		// do test
		testTitle(statutesTitles);
		statutesTitles = new StatutesTitles("bpc", "Bus. & Professions", "business and professions code", "california business and professions code", new String[]{"bus. & prof. code"});
		testTitle(statutesTitles);
		statutesTitles = new StatutesTitles(statutesTitles);
		testTitle(statutesTitles);
	}
	
	private void testTitle(StatutesTitles statutesTitles) {
		assertEquals("bpc", statutesTitles.getLawCode() );
		assertEquals("Bus. & Professions", statutesTitles.getShortTitle() );
		assertEquals("business and professions code", statutesTitles.getCommonTitle() );
		assertEquals("california business and professions code", statutesTitles.getFullTitle() );
		assertArrayEquals(new String[]{"bus. & prof. code"}, statutesTitles.getAbvrTitles());
		assertEquals("bus. & prof. code", statutesTitles.getAbvrTitle(0));
		assertEquals("bus. & prof. code", statutesTitles.getAbvrTitle());

	}
	

}

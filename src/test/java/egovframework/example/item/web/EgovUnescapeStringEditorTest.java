package egovframework.example.item.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.egovframe.rte.ptl.mvc.filter.HTMLTagFilterRequestWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * [상품관리][EgovUnescapeStringEditor] HTMLTagFilter 역변환 테스트
 */
class EgovUnescapeStringEditorTest {

	private static String throughFilter(String raw) {
		return new HTMLTagFilterRequestWrapper(new MockHttpServletRequest()).getSafeParamData(raw);
	}

	private static Object bind(String text) {
		final EgovUnescapeStringEditor editor = new EgovUnescapeStringEditor();
		editor.setAsText(text);
		return editor.getValue();
	}

	@Test
	void restoresOriginalInputAfterFilter() {
		for (final String raw : new String[] { "M&M's", "<script>alert(\"x\")</script>", "a & b && c",
				"literal &lt; &amp;amp; &quot;", "https://example.com/a.png?w=1&h=2", "평범한 상품명", "" }) {
			assertEquals(raw, bind(throughFilter(raw)), raw);
		}
	}

	@Test
	void trimsAndKeepsNull() {
		assertEquals("abc", bind("  abc  "));
		assertNull(bind(null));
	}

}

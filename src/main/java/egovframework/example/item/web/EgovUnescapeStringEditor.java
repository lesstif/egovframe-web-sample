/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.example.item.web;

import java.beans.PropertyEditorSupport;

/**
 * 요청 파라미터를 trim 하고 HTMLTagFilter 의 이스케이프를 되돌리는 문자열 에디터
 *
 * <p>
 * web.xml 의 HTMLTagFilter 는 *.do 요청 파라미터의 &amp; &lt; &gt; " ' 를 엔티티로 바꾼다.
 * 상품명 "M&amp;M's" 같은 값이 엔티티 상태로 저장되면 화면에서 c:out 이 한 번 더 이스케이프해
 * 엔티티가 그대로 보이고, 길이도 늘어 컬럼폭 검증과 어긋난다. 그래서 바인딩 시 원문으로 되돌리고
 * 출력은 JSP 에서 항상 이스케이프한다.
 * </p>
 *
 * @author 표준프레임워크센터
 * @since 2026.11.22
 * @version 1.0
 */
public class EgovUnescapeStringEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(String text) {
		setValue(text == null ? null : unescape(text.trim()));
	}

	@Override
	public String getAsText() {
		final Object value = getValue();
		return value == null ? "" : value.toString();
	}

	/**
	 * HTMLTagFilterRequestWrapper.getSafeParamData 의 역변환.
	 * &amp;amp; 를 마지막에 풀어야 "&amp;lt;" 처럼 사용자가 직접 입력한 엔티티 문자열이 보존된다.
	 */
	static String unescape(String value) {
		if (value.indexOf('&') < 0) {
			return value;
		}
		return value.replace("&lt;", "<")
				.replace("&gt;", ">")
				.replace("&quot;", "\"")
				.replace("&apos;", "'")
				.replace("&amp;", "&");
	}

}

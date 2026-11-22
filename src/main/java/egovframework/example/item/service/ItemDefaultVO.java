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
package egovframework.example.item.service;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * 상품 목록 검색·정렬·페이징 조건 VO
 *
 * @author 표준프레임워크센터
 * @since 2026.11.22
 * @version 1.0
 */
@Getter
@Setter
public class ItemDefaultVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 검색조건 (name: 상품명, id: 상품ID) */
	private String searchCondition = "name";

	/** 검색어 */
	private String searchKeyword = "";

	/** 카테고리 필터 */
	private String searchCategory = "";

	/** 판매상태 필터 */
	private String searchStatus = "";

	/** 정렬 (latest, priceAsc, priceDesc, name) */
	private String sortOrder = "latest";

	/** 판매중지 상품 제외 여부 (쇼핑몰 화면에서만 true) */
	private boolean displayOnly;

	/** 현재페이지 */
	private int pageIndex = 1;

	/** 페이지당 레코드 수 */
	private int pageUnit = 10;

	/** 페이지 리스트 크기 */
	private int pageSize = 10;

	/** firstIndex */
	private int firstIndex = 1;

	/** lastIndex */
	private int lastIndex = 1;

	/** recordCountPerPage */
	private int recordCountPerPage = 10;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

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

import java.util.List;

/**
 * 상품관리 서비스
 *
 * @author 표준프레임워크센터
 * @since 2026.11.22
 * @version 1.0
 */
public interface EgovItemService {

	/**
	 * 상품을 등록한다.
	 * @param vo - 등록할 정보가 담긴 ItemVO
	 */
	void insertItem(ItemVO vo);

	/**
	 * 상품을 수정한다.
	 * @param vo - 수정할 정보가 담긴 ItemVO
	 */
	void updateItem(ItemVO vo);

	/**
	 * 상품을 삭제한다.
	 * @param vo - 삭제할 상품ID가 담긴 ItemVO
	 */
	void deleteItem(ItemVO vo);

	/**
	 * 상품을 조회한다. (관리자용, 판매중지 포함)
	 * @param vo - 조회할 상품ID가 담긴 ItemVO
	 * @return 조회한 상품
	 */
	ItemVO selectItem(ItemVO vo);

	/**
	 * 쇼핑몰에 노출되는 상품을 조회한다. 판매중지 상품은 없는 상품으로 본다.
	 * @param vo - 조회할 상품ID가 담긴 ItemVO
	 * @return 조회한 상품
	 */
	ItemVO selectDisplayItem(ItemVO vo);

	/**
	 * 상품 목록을 조회한다.
	 * @param vo - 검색조건이 담긴 VO
	 * @return 상품 목록
	 */
	List<ItemVO> selectItemList(ItemDefaultVO vo);

	/**
	 * 상품 총 개수를 조회한다.
	 * @param vo - 검색조건이 담긴 VO
	 * @return 상품 총 개수
	 */
	int selectItemListTotCnt(ItemDefaultVO vo);

}

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
package egovframework.example.item.service.impl;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.mapper.EgovMapper;

import egovframework.example.item.service.ItemDefaultVO;
import egovframework.example.item.service.ItemVO;

/**
 * 상품 데이터처리 매퍼
 *
 * @author 표준프레임워크센터
 * @since 2026.11.22
 * @version 1.0
 */
@EgovMapper("itemMapper")
public interface ItemMapper {

	void insertItem(ItemVO vo);

	int updateItem(ItemVO vo);

	int deleteItem(ItemVO vo);

	ItemVO selectItem(ItemVO vo);

	List<ItemVO> selectItemList(ItemDefaultVO vo);

	int selectItemListTotCnt(ItemDefaultVO vo);

}

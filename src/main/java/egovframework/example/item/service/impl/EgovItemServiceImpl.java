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

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.cmmn.exception.BaseRuntimeException;
import org.egovframe.rte.fdl.cmmn.exception.FdlException;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.springframework.stereotype.Service;

import egovframework.example.item.service.EgovItemService;
import egovframework.example.item.service.ItemDefaultVO;
import egovframework.example.item.service.ItemStatus;
import egovframework.example.item.service.ItemVO;
import jakarta.annotation.Resource;

/**
 * 상품관리 서비스 구현
 *
 * @author 표준프레임워크센터
 * @since 2026.11.22
 * @version 1.0
 */
@Service("itemService")
public class EgovItemServiceImpl extends EgovAbstractServiceImpl implements EgovItemService {

	@Resource(name = "itemMapper")
	private ItemMapper itemMapper;

	@Resource(name = "itemIdGnrService")
	private EgovIdGnrService itemIdGnrService;

	@Override
	public void insertItem(ItemVO vo) {
		try {
			vo.setItemId(itemIdGnrService.getNextStringId());
		} catch (FdlException e) {
			throw new BaseRuntimeException(e);
		}
		applySoldOut(vo);
		itemMapper.insertItem(vo);
	}

	@Override
	public void updateItem(ItemVO vo) {
		applySoldOut(vo);
		if (itemMapper.updateItem(vo) == 0) {
			throw new BaseRuntimeException(processException("info.nodata.msg"));
		}
	}

	@Override
	public void deleteItem(ItemVO vo) {
		if (itemMapper.deleteItem(vo) == 0) {
			throw new BaseRuntimeException(processException("info.nodata.msg"));
		}
	}

	@Override
	public ItemVO selectItem(ItemVO vo) {
		final ItemVO resultVO = itemMapper.selectItem(vo);
		if (resultVO == null) {
			throw new BaseRuntimeException(processException("info.nodata.msg"));
		}
		return resultVO;
	}

	@Override
	public ItemVO selectDisplayItem(ItemVO vo) {
		final ItemVO resultVO = itemMapper.selectItem(vo);
		if (resultVO == null || ItemStatus.STOP.name().equals(resultVO.getStatusCode())) {
			throw new BaseRuntimeException(processException("info.nodata.msg"));
		}
		return resultVO;
	}

	@Override
	public List<ItemVO> selectItemList(ItemDefaultVO vo) {
		return itemMapper.selectItemList(vo);
	}

	@Override
	public int selectItemListTotCnt(ItemDefaultVO vo) {
		return itemMapper.selectItemListTotCnt(vo);
	}

	/**
	 * 재고가 0인 판매중 상품은 품절로 바꾼다. 판매중지는 관리자가 정한 상태이므로 그대로 둔다.
	 */
	private static void applySoldOut(ItemVO vo) {
		if (ItemStatus.SALE.name().equals(vo.getStatusCode()) && vo.getStockQty() != null && vo.getStockQty() == 0) {
			vo.setStatusCode(ItemStatus.SOLDOUT.name());
		}
	}

}

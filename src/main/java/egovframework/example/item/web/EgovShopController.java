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

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import egovframework.example.item.service.EgovItemService;
import egovframework.example.item.service.ItemCategory;
import egovframework.example.item.service.ItemVO;
import jakarta.annotation.Resource;

/**
 * 쇼핑몰(고객) 상품 진열 컨트롤러
 *
 * @author 표준프레임워크센터
 * @since 2026.11.22
 * @version 1.0
 */
@Controller
public class EgovShopController {

	static final String LIST_VIEW = "item/egovShopItemList";

	static final String DETAIL_VIEW = "item/egovShopItemDetail";

	@Resource(name = "itemService")
	private EgovItemService itemService;

	@Resource(name = "propertiesService")
	private EgovPropertyService propertiesService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EgovUnescapeStringEditor());
	}

	@ModelAttribute("categoryList")
	public ItemCategory[] categoryList() {
		return ItemCategory.values();
	}

	/**
	 * 쇼핑몰 상품 목록을 조회한다. 판매중지 상품은 노출하지 않는다.
	 * @param itemVO - 카테고리, 검색어, 정렬, 페이지
	 * @param model
	 * @return 상품 진열 화면
	 */
	@GetMapping("/shop/itemList.do")
	public String selectShopItemList(@ModelAttribute("itemVO") ItemVO itemVO, Model model) {
		itemVO.setDisplayOnly(true);
		itemVO.setSearchStatus("");
		itemVO.setSearchCondition("name");
		itemVO.setPageUnit(propertiesService.getInt("shopPageUnit"));
		itemVO.setPageSize(propertiesService.getInt("pageSize"));

		final PaginationInfo paginationInfo = EgovItemController.paginate(itemVO);

		model.addAttribute("resultList", itemService.selectItemList(itemVO));
		paginationInfo.setTotalRecordCount(itemService.selectItemListTotCnt(itemVO));
		model.addAttribute("paginationInfo", paginationInfo);

		return LIST_VIEW;
	}

	/**
	 * 쇼핑몰 상품 상세를 조회한다.
	 * @param itemVO - 상품ID와 목록 상태
	 * @param model
	 * @return 상품 상세 화면
	 */
	@GetMapping("/shop/itemDetail.do")
	public String selectShopItem(@ModelAttribute("itemVO") ItemVO itemVO, Model model) {
		model.addAttribute("item", itemService.selectDisplayItem(itemVO));
		return DETAIL_VIEW;
	}

}

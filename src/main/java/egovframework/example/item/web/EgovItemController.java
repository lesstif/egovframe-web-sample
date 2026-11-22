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

import java.util.List;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import egovframework.example.item.service.EgovItemService;
import egovframework.example.item.service.ItemCategory;
import egovframework.example.item.service.ItemDefaultVO;
import egovframework.example.item.service.ItemStatus;
import egovframework.example.item.service.ItemVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 상품관리(관리자) 컨트롤러
 *
 * @author 표준프레임워크센터
 * @since 2026.11.22
 * @version 1.0
 */
@Controller
public class EgovItemController {

	static final String LIST_VIEW = "item/egovItemList";

	static final String REGISTER_VIEW = "item/egovItemRegister";

	static final String REDIRECT_LIST = "redirect:/item/egovItemList.do";

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

	@ModelAttribute("statusList")
	public ItemStatus[] statusList() {
		return ItemStatus.values();
	}

	/**
	 * 상품 목록을 조회한다.
	 * @param itemVO - 검색조건
	 * @param model
	 * @return 상품 목록 화면
	 */
	@GetMapping("/item/egovItemList.do")
	public String selectItemList(@ModelAttribute("itemVO") ItemVO itemVO, Model model) {
		itemVO.setDisplayOnly(false);
		itemVO.setPageUnit(propertiesService.getInt("pageUnit"));
		itemVO.setPageSize(propertiesService.getInt("pageSize"));

		final PaginationInfo paginationInfo = paginate(itemVO);

		model.addAttribute("resultList", itemService.selectItemList(itemVO));
		paginationInfo.setTotalRecordCount(itemService.selectItemListTotCnt(itemVO));
		model.addAttribute("paginationInfo", paginationInfo);

		return LIST_VIEW;
	}

	/**
	 * 상품 등록 화면을 조회한다.
	 * @param itemVO - 목록 검색조건
	 * @return 상품 등록 화면
	 */
	@PostMapping("/item/addItemView.do")
	public String addItemView(@ModelAttribute("itemVO") ItemVO itemVO) {
		itemVO.setStatusCode(ItemStatus.SALE.name());
		itemVO.setStockQty(0);
		return REGISTER_VIEW;
	}

	/**
	 * 상품을 등록한다.
	 * @param itemVO - 등록할 상품
	 * @return 상품 목록으로 redirect
	 */
	@PostMapping("/item/addItem.do")
	public String addItem(@Valid @ModelAttribute("itemVO") ItemVO itemVO, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, SessionStatus status) {
		validateCodes(itemVO, bindingResult);
		if (bindingResult.hasErrors()) {
			return REGISTER_VIEW;
		}

		itemService.insertItem(itemVO);
		status.setComplete();
		keepListState(itemVO, redirectAttributes);
		redirectAttributes.addFlashAttribute("resultMessage", "add.confirm.success");
		return REDIRECT_LIST;
	}

	/**
	 * 상품 수정 화면을 조회한다.
	 * @param itemVO - 조회할 상품ID와 목록 검색조건
	 * @param model
	 * @return 상품 수정 화면
	 */
	@PostMapping("/item/updateItemView.do")
	public String updateItemView(@ModelAttribute("itemVO") ItemVO itemVO, Model model) {
		final ItemVO detail = itemService.selectItem(itemVO);
		copyListState(itemVO, detail);
		model.addAttribute("itemVO", detail);
		return REGISTER_VIEW;
	}

	/**
	 * 상품을 수정한다.
	 * @param itemVO - 수정할 상품
	 * @return 상품 목록으로 redirect
	 */
	@PostMapping("/item/updateItem.do")
	public String updateItem(@Valid @ModelAttribute("itemVO") ItemVO itemVO, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, SessionStatus status) {
		validateCodes(itemVO, bindingResult);
		if (bindingResult.hasErrors()) {
			return REGISTER_VIEW;
		}

		itemService.updateItem(itemVO);
		status.setComplete();
		keepListState(itemVO, redirectAttributes);
		redirectAttributes.addFlashAttribute("resultMessage", "modify.confirm.success");
		return REDIRECT_LIST;
	}

	/**
	 * 상품을 삭제한다.
	 * @param itemVO - 삭제할 상품ID와 목록 검색조건
	 * @return 상품 목록으로 redirect
	 */
	@PostMapping("/item/deleteItem.do")
	public String deleteItem(@ModelAttribute("itemVO") ItemVO itemVO, RedirectAttributes redirectAttributes,
			SessionStatus status) {
		itemService.deleteItem(itemVO);
		status.setComplete();
		keepListState(itemVO, redirectAttributes);
		redirectAttributes.addFlashAttribute("resultMessage", "delete.confirm.success");
		return REDIRECT_LIST;
	}

	/**
	 * Bean Validation 으로 표현하기 어려운 코드값과 필드 간 규칙을 검사한다.
	 */
	static void validateCodes(ItemVO itemVO, BindingResult bindingResult) {
		if (!bindingResult.hasFieldErrors("categoryCode") && !ItemCategory.contains(itemVO.getCategoryCode())) {
			bindingResult.rejectValue("categoryCode", "item.invalid.categoryCode");
		}
		if (!bindingResult.hasFieldErrors("statusCode") && !ItemStatus.contains(itemVO.getStatusCode())) {
			bindingResult.rejectValue("statusCode", "item.invalid.statusCode");
		}
		if (!bindingResult.hasFieldErrors("salePrice") && !bindingResult.hasFieldErrors("price")
				&& itemVO.getSalePrice() != null && itemVO.getPrice() != null
				&& itemVO.getSalePrice() > itemVO.getPrice()) {
			bindingResult.rejectValue("salePrice", "item.invalid.salePrice");
		}
	}

	static PaginationInfo paginate(ItemDefaultVO searchVO) {
		final PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(Math.max(1, searchVO.getPageIndex()));
		paginationInfo.setRecordCountPerPage(searchVO.getPageUnit());
		paginationInfo.setPageSize(searchVO.getPageSize());

		searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
		searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
		return paginationInfo;
	}

	private static void copyListState(ItemDefaultVO from, ItemDefaultVO to) {
		to.setSearchCondition(from.getSearchCondition());
		to.setSearchKeyword(from.getSearchKeyword());
		to.setSearchCategory(from.getSearchCategory());
		to.setSearchStatus(from.getSearchStatus());
		to.setSortOrder(from.getSortOrder());
		to.setPageIndex(from.getPageIndex());
	}

	private static void keepListState(ItemDefaultVO itemVO, RedirectAttributes redirectAttributes) {
		for (final Object[] attr : List.of(
				new Object[] { "searchCondition", itemVO.getSearchCondition() },
				new Object[] { "searchKeyword", itemVO.getSearchKeyword() },
				new Object[] { "searchCategory", itemVO.getSearchCategory() },
				new Object[] { "searchStatus", itemVO.getSearchStatus() },
				new Object[] { "sortOrder", itemVO.getSortOrder() },
				new Object[] { "pageIndex", itemVO.getPageIndex() })) {
			if (attr[1] != null) {
				redirectAttributes.addAttribute((String) attr[0], attr[1]);
			}
		}
	}

}

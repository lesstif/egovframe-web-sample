package egovframework.example.item.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.support.SimpleSessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import egovframework.example.item.service.EgovItemService;
import egovframework.example.item.service.ItemDefaultVO;
import egovframework.example.item.service.ItemVO;

/**
 * [상품관리][EgovItemController] 등록·수정·삭제 후 목록 상태 유지 테스트
 */
class EgovItemControllerRedirectTest {

	private final List<String> calls = new ArrayList<>();

	private final EgovItemService stubService = new EgovItemService() {
		@Override
		public void insertItem(ItemVO vo) {
			calls.add("insert");
		}

		@Override
		public void updateItem(ItemVO vo) {
			calls.add("update");
		}

		@Override
		public void deleteItem(ItemVO vo) {
			calls.add("delete");
		}

		@Override
		public ItemVO selectItem(ItemVO vo) {
			return vo;
		}

		@Override
		public ItemVO selectDisplayItem(ItemVO vo) {
			return vo;
		}

		@Override
		public List<ItemVO> selectItemList(ItemDefaultVO vo) {
			return Collections.emptyList();
		}

		@Override
		public int selectItemListTotCnt(ItemDefaultVO vo) {
			return 0;
		}
	};

	private EgovItemController controller() throws ReflectiveOperationException {
		final EgovItemController controller = new EgovItemController();
		final Field field = EgovItemController.class.getDeclaredField("itemService");
		field.setAccessible(true);
		field.set(controller, stubService);
		return controller;
	}

	private static ItemVO itemVO() {
		final ItemVO vo = new ItemVO();
		vo.setItemId("ITEM-00003");
		vo.setItemName("test");
		vo.setCategoryCode("FASHION");
		vo.setStatusCode("SALE");
		vo.setPrice(1000);
		vo.setStockQty(1);
		vo.setRegUser("test");
		vo.setSearchCondition("id");
		vo.setSearchKeyword("0003");
		vo.setSearchCategory("FASHION");
		vo.setSearchStatus("SALE");
		vo.setSortOrder("priceAsc");
		vo.setPageIndex(3);
		return vo;
	}

	@Test
	void addUpdateDeleteRedirectWithSameListState() throws ReflectiveOperationException {
		final EgovItemController controller = controller();
		final RedirectAttributesModelMap addAttrs = new RedirectAttributesModelMap();
		final RedirectAttributesModelMap updateAttrs = new RedirectAttributesModelMap();
		final RedirectAttributesModelMap deleteAttrs = new RedirectAttributesModelMap();

		final ItemVO add = itemVO();
		final ItemVO update = itemVO();
		final String addView = controller.addItem(add, new BeanPropertyBindingResult(add, "itemVO"), addAttrs,
				new SimpleSessionStatus());
		final String updateView = controller.updateItem(update, new BeanPropertyBindingResult(update, "itemVO"),
				updateAttrs, new SimpleSessionStatus());
		final String deleteView = controller.deleteItem(itemVO(), deleteAttrs, new SimpleSessionStatus());

		assertEquals(List.of("insert", "update", "delete"), calls);
		assertEquals("redirect:/item/egovItemList.do", addView);
		assertEquals(addView, updateView);
		assertEquals(addView, deleteView);

		final Map<String, Object> expected = Map.of("searchCondition", "id", "searchKeyword", "0003",
				"searchCategory", "FASHION", "searchStatus", "SALE", "sortOrder", "priceAsc", "pageIndex", "3");
		assertEquals(expected, addAttrs);
		assertEquals(expected, updateAttrs);
		assertEquals(expected, deleteAttrs);

		assertEquals("add.confirm.success", addAttrs.getFlashAttributes().get("resultMessage"));
		assertEquals("modify.confirm.success", updateAttrs.getFlashAttributes().get("resultMessage"));
		assertEquals("delete.confirm.success", deleteAttrs.getFlashAttributes().get("resultMessage"));
	}

	@Test
	void invalidInputStaysOnRegisterViewWithoutSaving() throws ReflectiveOperationException {
		final ItemVO vo = itemVO();
		vo.setSalePrice(5000);
		final BeanPropertyBindingResult result = new BeanPropertyBindingResult(vo, "itemVO");

		final String view = controller().addItem(vo, result, new RedirectAttributesModelMap(),
				new SimpleSessionStatus());

		assertEquals("item/egovItemRegister", view);
		assertTrue(result.hasFieldErrors("salePrice"));
		assertTrue(calls.isEmpty());
	}

}

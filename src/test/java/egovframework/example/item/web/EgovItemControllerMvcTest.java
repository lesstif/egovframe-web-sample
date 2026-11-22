package egovframework.example.item.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import egovframework.example.item.service.ItemVO;
import egovframework.test.EgovTestAbstractSpringMvc;

/**
 * [상품관리][EgovItemController, EgovShopController] MockMvc 테스트
 *
 * <p>
 * MockMvc 요청은 트랜잭션 롤백이 없으므로 이 클래스에서는 데이터를 바꾸는 성공 요청을 보내지 않는다.
 * </p>
 */
class EgovItemControllerMvcTest extends EgovTestAbstractSpringMvc {

	/**
	 * 유효한 등록 파라미터에 overrides 를 덮어쓴 요청. MockMvc 의 param() 은 값을 추가하므로 미리 합친다.
	 */
	private static MockHttpServletRequestBuilder add(String... overrides) {
		final Map<String, String> params = new LinkedHashMap<>();
		params.put("itemName", "테스트 상품");
		params.put("categoryCode", "FOOD");
		params.put("statusCode", "SALE");
		params.put("price", "10000");
		params.put("salePrice", "");
		params.put("stockQty", "5");
		params.put("imageUrl", "");
		params.put("regUser", "test");
		for (int i = 0; i < overrides.length; i += 2) {
			params.put(overrides[i], overrides[i + 1]);
		}
		final MockHttpServletRequestBuilder builder = post("/item/addItem.do");
		params.forEach(builder::param);
		return builder;
	}

	@SuppressWarnings("unchecked")
	private static List<ItemVO> resultList(MvcResult result) {
		return (List<ItemVO>) result.getModelAndView().getModel().get("resultList");
	}

	private static ItemVO modelItem(MvcResult result, String name) {
		return (ItemVO) result.getModelAndView().getModel().get(name);
	}

	private static String viewName(MvcResult result) {
		return result.getModelAndView().getViewName();
	}

	private static void assertFieldError(MvcResult result, String field, String code) {
		final BindingResult errors = (BindingResult) result.getModelAndView().getModel()
				.get(BindingResult.MODEL_KEY_PREFIX + "itemVO");
		final FieldError error = errors.getFieldError(field);
		assertThat(error).as(field).isNotNull();
		assertThat(error.getCodes()).as(field).contains(code);
	}

	@Test
	void adminListShowsFirstPageWithAllStatuses() throws Exception {
		final MvcResult all = mockMvc.perform(get("/item/egovItemList.do")).andReturn();
		assertThat(all.getResponse().getStatus()).isEqualTo(200);
		assertThat(viewName(all)).isEqualTo("item/egovItemList");
		assertThat(all.getModelAndView().getModel()).containsKeys("paginationInfo", "categoryList", "statusList");
		assertThat(resultList(all)).hasSize(10);

		final MvcResult stopped = mockMvc.perform(get("/item/egovItemList.do").param("searchStatus", "STOP"))
				.andReturn();
		assertThat(resultList(stopped)).isNotEmpty().allMatch(i -> "STOP".equals(i.getStatusCode()));
	}

	@Test
	void shopListExcludesStoppedItemsEvenWhenRequested() throws Exception {
		final MvcResult result = mockMvc.perform(get("/shop/itemList.do").param("searchStatus", "STOP")
				.param("displayOnly", "false")).andReturn();
		assertThat(viewName(result)).isEqualTo("item/egovShopItemList");
		assertThat(resultList(result)).hasSize(12).noneMatch(i -> "STOP".equals(i.getStatusCode()));
	}

	@Test
	void shopDetailOfStoppedItemShowsErrorView() throws Exception {
		final MvcResult result = mockMvc.perform(get("/shop/itemDetail.do").param("itemId", "ITEM-00008"))
				.andReturn();
		assertThat(viewName(result)).isEqualTo("item/egovShopItemDetail");
		assertThat(modelItem(result, "item").getItemName()).isEqualTo("무선 블루투스 이어폰");

		final MvcResult stopped = mockMvc.perform(get("/shop/itemDetail.do").param("itemId", "ITEM-00012"))
				.andReturn();
		assertThat(viewName(stopped)).isEqualTo("cmmn/egovError");
	}

	@Test
	void addRejectsSalePriceAbovePrice() throws Exception {
		final MvcResult result = mockMvc.perform(add("salePrice", "20000")).andReturn();
		assertThat(result.getResponse().getStatus()).isEqualTo(200);
		assertThat(viewName(result)).isEqualTo("item/egovItemRegister");
		assertFieldError(result, "salePrice", "item.invalid.salePrice");
	}

	@Test
	void addRejectsInvalidInput() throws Exception {
		final MvcResult result = mockMvc.perform(add("price", "abc", "stockQty", "-1",
				"categoryCode", "WEAPON", "statusCode", "",
				"imageUrl", "javascript:alert(1)", "itemName", " ")).andReturn();
		assertThat(viewName(result)).isEqualTo("item/egovItemRegister");
		assertFieldError(result, "price", "typeMismatch");
		assertFieldError(result, "stockQty", "Min");
		assertFieldError(result, "categoryCode", "item.invalid.categoryCode");
		assertFieldError(result, "statusCode", "NotBlank");
		assertFieldError(result, "imageUrl", "Pattern");
		assertFieldError(result, "itemName", "NotBlank");
	}

	@Test
	void addUnescapesHtmlTagFilterOutput() throws Exception {
		// HTMLTagFilter 를 거친 것과 같은 파라미터. 가격 오류로 등록 화면에 머물러 바인딩 값을 확인한다.
		final MvcResult result = mockMvc
				.perform(add("itemName", "M&amp;M&apos;s &lt;초콜릿&gt;", "price", "x"))
				.andReturn();
		assertThat(viewName(result)).isEqualTo("item/egovItemRegister");
		assertThat(modelItem(result, "itemVO").getItemName()).isEqualTo("M&M's <초콜릿>");
	}

	@Test
	void updateViewKeepsListStateAndRejectsMissingItem() throws Exception {
		final MvcResult missing = mockMvc.perform(post("/item/updateItemView.do").param("itemId", "ITEM-99999"))
				.andReturn();
		assertThat(viewName(missing)).isEqualTo("cmmn/egovError");

		final MvcResult result = mockMvc.perform(post("/item/updateItemView.do").param("itemId", "ITEM-00003")
				.param("pageIndex", "2").param("searchKeyword", "패딩")).andReturn();
		assertThat(viewName(result)).isEqualTo("item/egovItemRegister");
		final ItemVO itemVO = modelItem(result, "itemVO");
		assertThat(itemVO.getPageIndex()).isEqualTo(2);
		assertThat(itemVO.getSearchKeyword()).isEqualTo("패딩");
		assertThat(itemVO.getItemName()).contains("패딩");
	}

}

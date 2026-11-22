package egovframework.example.item.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.egovframe.rte.fdl.cmmn.exception.BaseRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;

import egovframework.example.item.service.EgovItemService;
import egovframework.example.item.service.ItemDefaultVO;
import egovframework.example.item.service.ItemVO;
import egovframework.test.EgovTestAbstractSpring;

/**
 * [상품관리][EgovItemServiceImpl] 등록·수정·삭제·조회와 목록 검색 테스트
 *
 * <p>
 * EgovTestAbstractSpring 이 @Transactional 이므로 각 테스트의 변경은 롤백된다.
 * 기대값은 db/itemdb.sql 의 시드 데이터(25건, 판매중지 2건, DIGITAL 5건)를 기준으로 한다.
 * </p>
 */
@ContextConfiguration(classes = { EgovItemServiceImplTest.class, EgovTestAbstractSpring.class })
@Configuration
@ImportResource({ "classpath*:egovframework/spring/context-idgen.xml", })
@ComponentScan(useDefaultFilters = false, basePackages = {
		"egovframework.example.item.service.impl", }, includeFilters = {
				@Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { EgovItemServiceImpl.class, ItemMapper.class, }) })
class EgovItemServiceImplTest extends EgovTestAbstractSpring {

	@Autowired
	private EgovItemService itemService;

	private static ItemVO newItem(String name, int price, Integer salePrice, int stock, String status) {
		final ItemVO vo = new ItemVO();
		vo.setItemName(name);
		vo.setCategoryCode("DIGITAL");
		vo.setPrice(price);
		vo.setSalePrice(salePrice);
		vo.setStockQty(stock);
		vo.setStatusCode(status);
		vo.setDescription("테스트 상품 M&M's <b>");
		vo.setRegUser("test");
		return vo;
	}

	private static ItemVO idOf(String itemId) {
		final ItemVO vo = new ItemVO();
		vo.setItemId(itemId);
		return vo;
	}

	private static ItemDefaultVO search() {
		final ItemDefaultVO vo = new ItemDefaultVO();
		vo.setFirstIndex(0);
		vo.setRecordCountPerPage(100);
		return vo;
	}

	@Test
	void insertAssignsIdAndStoresValues() {
		final ItemVO vo = newItem("테스트 이어폰", 50000, 40000, 3, "SALE");

		itemService.insertItem(vo);

		assertTrue(vo.getItemId().matches("ITEM-\\d{5}"), "itemId=" + vo.getItemId());
		final ItemVO saved = itemService.selectItem(idOf(vo.getItemId()));
		assertEquals("테스트 이어폰", saved.getItemName());
		assertEquals(50000, saved.getPrice());
		assertEquals(40000, saved.getSalePrice());
		assertEquals(3, saved.getStockQty());
		assertEquals("SALE", saved.getStatusCode());
		assertEquals("테스트 상품 M&M's <b>", saved.getDescription());
		assertNotNull(saved.getRegDate());
		assertNull(saved.getUpdDate());
		assertTrue(saved.isOnSale());
		assertEquals(40000, saved.getSellingPrice());
		assertEquals(20, saved.getDiscountRate());
		assertTrue(saved.isPurchasable());
	}

	@Test
	void insertWithZeroStockOnSaleBecomesSoldOut() {
		final ItemVO vo = newItem("재고없음", 1000, null, 0, "SALE");

		itemService.insertItem(vo);

		final ItemVO saved = itemService.selectItem(idOf(vo.getItemId()));
		assertEquals("SOLDOUT", saved.getStatusCode());
		assertFalse(saved.isPurchasable());
		assertFalse(saved.isOnSale());
		assertEquals(1000, saved.getSellingPrice());
	}

	@Test
	void zeroStockKeepsStopStatus() {
		final ItemVO vo = newItem("판매중지", 1000, null, 0, "STOP");

		itemService.insertItem(vo);

		assertEquals("STOP", itemService.selectItem(idOf(vo.getItemId())).getStatusCode());
	}

	@Test
	void updateChangesFieldsAndSetsUpdDate() {
		final ItemVO vo = itemService.selectItem(idOf("ITEM-00002"));
		vo.setItemName("수정된 데님");
		vo.setSalePrice(39000);
		vo.setStockQty(0);
		vo.setStatusCode("SALE");

		itemService.updateItem(vo);

		final ItemVO saved = itemService.selectItem(idOf("ITEM-00002"));
		assertEquals("수정된 데님", saved.getItemName());
		assertEquals(39000, saved.getSalePrice());
		assertEquals("SOLDOUT", saved.getStatusCode());
		assertNotNull(saved.getUpdDate());
		assertEquals("eGov", saved.getRegUser(), "등록자는 수정하지 않는다.");
	}

	@Test
	void updateOrDeleteMissingItemFails() {
		final ItemVO missing = newItem("없음", 1, null, 1, "SALE");
		missing.setItemId("ITEM-99999");

		assertThrows(BaseRuntimeException.class, () -> itemService.updateItem(missing));
		assertThrows(BaseRuntimeException.class, () -> itemService.deleteItem(missing));
	}

	@Test
	void deleteRemovesItem() {
		itemService.deleteItem(idOf("ITEM-00001"));

		assertThrows(BaseRuntimeException.class, () -> itemService.selectItem(idOf("ITEM-00001")));
	}

	@Test
	void displayItemHidesStoppedItem() {
		assertEquals("ITEM-00008", itemService.selectDisplayItem(idOf("ITEM-00008")).getItemId());
		assertEquals("SOLDOUT", itemService.selectDisplayItem(idOf("ITEM-00010")).getStatusCode());
		assertThrows(BaseRuntimeException.class, () -> itemService.selectDisplayItem(idOf("ITEM-00012")));
		assertThrows(BaseRuntimeException.class, () -> itemService.selectDisplayItem(idOf("ITEM-99999")));
	}

	@Test
	void listFiltersAndCounts() {
		final ItemDefaultVO all = search();
		assertEquals(25, itemService.selectItemListTotCnt(all));
		assertEquals(25, itemService.selectItemList(all).size());

		final ItemDefaultVO display = search();
		display.setDisplayOnly(true);
		assertEquals(23, itemService.selectItemListTotCnt(display));
		assertTrue(itemService.selectItemList(display).stream().noneMatch(i -> "STOP".equals(i.getStatusCode())));

		final ItemDefaultVO digital = search();
		digital.setSearchCategory("DIGITAL");
		assertEquals(5, itemService.selectItemListTotCnt(digital));
		digital.setDisplayOnly(true);
		assertEquals(4, itemService.selectItemListTotCnt(digital));

		final ItemDefaultVO soldOut = search();
		soldOut.setSearchStatus("SOLDOUT");
		assertEquals(4, itemService.selectItemListTotCnt(soldOut));

		final ItemDefaultVO keyword = search();
		keyword.setSearchKeyword("spf50");
		final List<ItemVO> found = itemService.selectItemList(keyword);
		assertEquals(1, found.size(), "상품명 검색은 대소문자를 구분하지 않는다.");
		assertEquals("ITEM-00006", found.get(0).getItemId());

		final ItemDefaultVO byId = search();
		byId.setSearchCondition("id");
		byId.setSearchKeyword("0001");
		assertEquals(11, itemService.selectItemListTotCnt(byId), "ITEM-00001, ITEM-00010~00019");
	}

	@Test
	void listSortsAndPages() {
		final ItemDefaultVO priceAsc = search();
		priceAsc.setSortOrder("priceAsc");
		final List<ItemVO> asc = itemService.selectItemList(priceAsc);
		for (int i = 1; i < asc.size(); i++) {
			assertTrue(asc.get(i - 1).getSellingPrice() <= asc.get(i).getSellingPrice(), "판매가 오름차순이어야 한다.");
		}

		final ItemDefaultVO priceDesc = search();
		priceDesc.setSortOrder("priceDesc");
		assertEquals("ITEM-00011", itemService.selectItemList(priceDesc).get(0).getItemId());

		final ItemDefaultVO latest = search();
		assertEquals("ITEM-00025", itemService.selectItemList(latest).get(0).getItemId());

		final ItemDefaultVO unknownSort = search();
		unknownSort.setSortOrder("ITEM_ID; DROP TABLE ITEM");
		assertEquals("ITEM-00025", itemService.selectItemList(unknownSort).get(0).getItemId(),
				"정의되지 않은 정렬값은 최신순으로 처리한다.");

		final ItemDefaultVO page3 = search();
		page3.setFirstIndex(20);
		page3.setRecordCountPerPage(10);
		assertEquals(5, itemService.selectItemList(page3).size());
	}

}

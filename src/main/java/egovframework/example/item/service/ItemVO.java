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

import java.util.Date;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 상품 VO
 *
 * <p>
 * 필드 길이 제약은 db/itemdb.sql 의 ITEM 테이블 컬럼폭과 같아야 한다.
 * </p>
 *
 * @author 표준프레임워크센터
 * @since 2026.11.22
 * @version 1.0
 */
@Getter
@Setter
public class ItemVO extends ItemDefaultVO {

	private static final long serialVersionUID = 1L;

	/** 가격 상한 (INTEGER 컬럼 범위 안에서 쇼핑몰에 맞는 값) */
	public static final int MAX_PRICE = 100_000_000;

	/** 재고 상한 */
	public static final int MAX_STOCK = 1_000_000;

	/** 상품ID */
	private String itemId;

	/** 상품명 */
	@NotBlank(message = "{item.required.itemName}")
	@Size(max = 100, message = "{item.size.itemName}")
	private String itemName;

	/** 카테고리코드 */
	@NotBlank(message = "{item.required.categoryCode}")
	private String categoryCode;

	/** 정가 */
	@NotNull(message = "{item.required.price}")
	@Min(value = 0, message = "{item.range.price}")
	@Max(value = MAX_PRICE, message = "{item.range.price}")
	private Integer price;

	/** 할인가 (없으면 정가로 판매) */
	@Min(value = 0, message = "{item.range.price}")
	@Max(value = MAX_PRICE, message = "{item.range.price}")
	private Integer salePrice;

	/** 재고수량 */
	@NotNull(message = "{item.required.stockQty}")
	@Min(value = 0, message = "{item.range.stockQty}")
	@Max(value = MAX_STOCK, message = "{item.range.stockQty}")
	private Integer stockQty;

	/** 판매상태코드 */
	@NotBlank(message = "{item.required.statusCode}")
	private String statusCode;

	/** 이미지 URL (http/https 또는 컨텍스트 기준 경로) */
	@Size(max = 500, message = "{item.size.imageUrl}")
	@Pattern(regexp = "^$|^(https?://|/(?!/))\\S*$", message = "{item.pattern.imageUrl}")
	private String imageUrl;

	/** 상품설명 */
	@Size(max = 2000, message = "{item.size.description}")
	private String description;

	/** 등록자 */
	@NotBlank(message = "{item.required.regUser}")
	@Size(max = 10, message = "{item.size.regUser}")
	private String regUser;

	/** 등록일시 */
	private Date regDate;

	/** 수정일시 */
	private Date updDate;

	/**
	 * 할인가가 정가보다 낮을 때만 할인 중으로 본다.
	 * @return 할인 여부
	 */
	public boolean isOnSale() {
		return price != null && salePrice != null && salePrice < price;
	}

	/**
	 * 실제 판매가를 구한다.
	 * @return 할인 중이면 할인가, 아니면 정가
	 */
	public Integer getSellingPrice() {
		return isOnSale() ? salePrice : price;
	}

	/**
	 * 할인율(%)을 구한다. 소수점 이하는 버린다.
	 * @return 할인율, 할인 중이 아니면 0
	 */
	public int getDiscountRate() {
		if (!isOnSale() || price == 0) {
			return 0;
		}
		return (int) ((price - (long) salePrice) * 100 / price);
	}

	/**
	 * 구매 가능 여부를 판단한다.
	 * @return 판매중이고 재고가 있으면 true
	 */
	public boolean isPurchasable() {
		return ItemStatus.SALE.name().equals(statusCode) && stockQty != null && stockQty > 0;
	}

}

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

import java.util.Arrays;

/**
 * 상품 판매상태. 화면 표시명은 메시지 item.status.{코드} 로 정의한다.
 *
 * @author 표준프레임워크센터
 * @since 2026.11.22
 * @version 1.0
 */
public enum ItemStatus {

	/** 판매중 */
	SALE,

	/** 품절 */
	SOLDOUT,

	/** 판매중지 (쇼핑몰에 노출하지 않음) */
	STOP;

	/**
	 * 코드가 정의된 판매상태인지 확인한다.
	 * @param code 판매상태코드
	 * @return 정의된 코드면 true
	 */
	public static boolean contains(String code) {
		return Arrays.stream(values()).anyMatch(s -> s.name().equals(code));
	}

}

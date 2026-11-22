<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
/**
* @Class Name : egovShopItemList.jsp
* @Description : 쇼핑몰 상품 진열 화면
* @Modification Information
*
*   수정일         수정자                   수정내용
*  -------    --------    ---------------------------
*  2026.11.22				최초 생성
*/
%>
<c:set var="navActive" value="shop"/>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="item.shop.title" /></title>

	<link type="text/css" rel="stylesheet" href="<c:url value='/css/component/output.css'/>" />
	<link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframe.css'/>" />
	<link type="text/css" rel="stylesheet" href="<c:url value='/css/item.css'/>" />

	<script type="text/javascript">
		function fn_egov_selectList() {
			const frm = document.shopForm;
			frm.pageIndex.value = 1;
			frm.submit();
		}

		function fn_egov_link_page(pageNo) {
			const frm = document.shopForm;
			frm.pageIndex.value = pageNo;
			frm.submit();
		}
	</script>
</head>

<body>
<div id="container" class="inner">

	<%@ include file="itemNav.jspf" %>

	<h2 class="heading-large"><spring:message code="item.shop.title" /></h2>

	<c:url var="shopListUrl" value="/shop/itemList.do"/>
	<form:form modelAttribute="itemVO" id="shopForm" name="shopForm" method="get" action="${shopListUrl}">
		<form:hidden path="pageIndex" />
		<form:hidden path="searchCategory" />

		<ul class="item-category-tabs" role="tablist">
			<li>
				<c:url var="allUrl" value="/shop/itemList.do">
					<c:param name="sortOrder" value="${itemVO.sortOrder}"/>
					<c:param name="searchKeyword" value="${itemVO.searchKeyword}"/>
				</c:url>
				<a href="<c:out value='${allUrl}'/>" role="tab" aria-selected="${empty itemVO.searchCategory}" class="${empty itemVO.searchCategory ? 'active' : ''}"><spring:message code="item.category.all" /></a>
			</li>
			<c:forEach var="category" items="${categoryList}">
				<c:url var="categoryUrl" value="/shop/itemList.do">
					<c:param name="searchCategory" value="${category}"/>
					<c:param name="sortOrder" value="${itemVO.sortOrder}"/>
					<c:param name="searchKeyword" value="${itemVO.searchKeyword}"/>
				</c:url>
				<c:set var="isActive" value="${itemVO.searchCategory == category.name()}"/>
				<li>
					<a href="<c:out value='${categoryUrl}'/>" role="tab" aria-selected="${isActive}" class="${isActive ? 'active' : ''}"><spring:message code="item.category.${category}" /></a>
				</li>
			</c:forEach>
		</ul>

		<div class="form-group">
		<div class="search-wrap item-shop-search">
			<div class="search-body">
				<div class="form-conts btn-ico-wrap searchKeyword">
					<spring:message code="search.keyword" var="keywordPlaceholder"/>
					<form:input path="searchKeyword" cssClass="krds-input medium" placeholder="${keywordPlaceholder}" maxlength="100" />
					<button type="button" class="krds-btn medium icon" onclick="fn_egov_selectList()">
						<span class="sr-only"><spring:message code="button.search" /></span>
						<i class="svg-icon ico-sch"></i>
					</button>
				</div>
				<div class="form-conts">
					<spring:message code="item.sort.label" var="sortLabel"/>
					<form:select path="sortOrder" cssClass="krds-form-select medium" title="${sortLabel}" onchange="fn_egov_selectList()">
						<c:forEach var="sort" items="${['latest', 'priceAsc', 'priceDesc', 'name']}">
							<form:option value="${sort}"><spring:message code="item.sort.${sort}" /></form:option>
						</c:forEach>
					</form:select>
				</div>
			</div>
		</div>
		</div>
	</form:form>

	<p class="item-total"><spring:message code="item.total.count" arguments="${paginationInfo.totalRecordCount}" /></p>

	<c:choose>
		<c:when test="${not empty resultList}">
			<ul class="item-grid">
				<c:forEach var="result" items="${resultList}">
					<c:url var="detailUrl" value="/shop/itemDetail.do">
						<c:param name="itemId" value="${result.itemId}"/>
						<c:param name="searchCategory" value="${itemVO.searchCategory}"/>
						<c:param name="searchKeyword" value="${itemVO.searchKeyword}"/>
						<c:param name="sortOrder" value="${itemVO.sortOrder}"/>
						<c:param name="pageIndex" value="${itemVO.pageIndex}"/>
					</c:url>
					<c:url var="imgSrc" value="${empty result.imageUrl ? '/img/item/category-'.concat(result.categoryCode).concat('.svg') : result.imageUrl}"/>
					<li class="item-card ${result.purchasable ? '' : 'soldout'}">
						<a href="<c:out value='${detailUrl}'/>">
							<div class="item-card-image">
								<img src="<c:out value='${imgSrc}'/>" alt="<c:out value='${result.itemName}'/>" loading="lazy" />
								<c:if test="${not result.purchasable}">
									<span class="item-card-soldout"><spring:message code="item.status.SOLDOUT" /></span>
								</c:if>
							</div>
							<div class="item-card-body">
								<span class="item-card-category"><spring:message code="item.category.${result.categoryCode}" text="${result.categoryCode}" /></span>
								<strong class="item-card-name"><c:out value="${result.itemName}"/></strong>
								<div class="item-price">
									<c:if test="${result.onSale}">
										<del class="item-price-origin"><fmt:formatNumber value="${result.price}" pattern="#,##0"/></del>
										<span class="item-discount">${result.discountRate}%</span>
									</c:if>
									<fmt:formatNumber var="sellingPrice" value="${result.sellingPrice}" pattern="#,##0"/>
									<strong class="item-price-selling"><spring:message code="item.price.format" arguments="${sellingPrice}" argumentSeparator="|" /></strong>
								</div>
							</div>
						</a>
					</li>
				</c:forEach>
			</ul>
		</c:when>
		<c:otherwise>
			<p class="item-empty"><spring:message code="info.nodata.msg" /></p>
		</c:otherwise>
	</c:choose>

	<div id="paging" class="krds-pagination w-page">
		<ui:pagination paginationInfo="${paginationInfo}" type="krds" jsFunction="fn_egov_link_page" />
	</div>

</div>
</body>
</html>

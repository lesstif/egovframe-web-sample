<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
/**
* @Class Name : egovItemList.jsp
* @Description : 상품관리 목록 화면
* @Modification Information
*
*   수정일         수정자                   수정내용
*  -------    --------    ---------------------------
*  2026.11.22				최초 생성
*/
%>
<c:set var="navActive" value="item"/>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="title.item.list" /></title>

	<link type="text/css" rel="stylesheet" href="<c:url value='/css/component/output.css'/>" />
	<link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframe.css'/>" />
	<link type="text/css" rel="stylesheet" href="<c:url value='/css/item.css'/>" />
	<script type="text/javascript" src="<c:url value='/js/jquery.min.js'/>" defer></script>

	<script type="text/javascript">
		function fn_egov_select(itemId) {
			const frm = document.listForm;
			frm.itemId.value = itemId;
			frm.action = "<c:url value='/item/updateItemView.do'/>";
			frm.method = "post";
			frm.submit();
		}

		function fn_egov_addView() {
			const frm = document.listForm;
			frm.action = "<c:url value='/item/addItemView.do'/>";
			frm.method = "post";
			frm.submit();
		}

		function fn_egov_selectList() {
			const frm = document.listForm;
			frm.pageIndex.value = 1;
			frm.action = "<c:url value='/item/egovItemList.do'/>";
			frm.method = "get";
			frm.submit();
		}

		function fn_egov_link_page(pageNo) {
			const frm = document.listForm;
			frm.pageIndex.value = pageNo;
			frm.action = "<c:url value='/item/egovItemList.do'/>";
			frm.method = "get";
			frm.submit();
		}
	</script>
</head>

<body>
<div id="container" class="inner">

	<%@ include file="itemNav.jspf" %>

	<h2 class="heading-large"><spring:message code="title.item.list" /></h2>

	<c:if test="${not empty resultMessage}">
		<p class="item-result-msg" role="status"><spring:message code="${resultMessage}" /></p>
	</c:if>

	<div id="content_pop">
		<form:form modelAttribute="itemVO" id="listForm" name="listForm" method="get" onsubmit="fn_egov_selectList(); return false;">
			<input type="hidden" id="itemId" name="itemId" />
			<form:hidden path="pageIndex" />

			<div class="form-group">
				<div class="search-wrap item-search">
					<div class="search-body">
						<div class="form-conts">
							<form:select path="searchCategory" cssClass="krds-form-select medium">
								<form:option value=""><spring:message code="item.category.all" /></form:option>
								<c:forEach var="category" items="${categoryList}">
									<form:option value="${category}"><spring:message code="item.category.${category}" /></form:option>
								</c:forEach>
							</form:select>
						</div>
						<div class="form-conts">
							<form:select path="searchStatus" cssClass="krds-form-select medium">
								<form:option value=""><spring:message code="item.status.all" /></form:option>
								<c:forEach var="itemStatus" items="${statusList}">
									<form:option value="${itemStatus}"><spring:message code="item.status.${itemStatus}" /></form:option>
								</c:forEach>
							</form:select>
						</div>
						<div class="form-conts">
							<form:select path="sortOrder" cssClass="krds-form-select medium">
								<c:forEach var="sort" items="${['latest', 'priceAsc', 'priceDesc', 'name']}">
									<form:option value="${sort}"><spring:message code="item.sort.${sort}" /></form:option>
								</c:forEach>
							</form:select>
						</div>
						<div class="form-conts">
							<form:select path="searchCondition" cssClass="krds-form-select medium">
								<form:option value="name"><spring:message code="item.search.name" /></form:option>
								<form:option value="id"><spring:message code="item.search.id" /></form:option>
							</form:select>
						</div>
						<div class="form-conts btn-ico-wrap searchKeyword">
							<spring:message code="search.keyword" var="keywordPlaceholder"/>
							<form:input path="searchKeyword" cssClass="krds-input medium" placeholder="${keywordPlaceholder}" maxlength="100" />
							<button type="button" class="krds-btn medium icon" onclick="fn_egov_selectList()">
								<span class="sr-only"><spring:message code="button.search" /></span>
								<i class="svg-icon ico-sch"></i>
							</button>
						</div>
					</div>
					<div class="page-btn-wrap">
						<a class="krds-btn medium secondary" href="<c:url value='/shop/itemList.do'/>"><spring:message code="button.shop" /></a>
						<button type="button" class="krds-btn medium" onclick="fn_egov_addView()"><spring:message code="button.create" /></button>
					</div>
				</div>
			</div>
		</form:form>

		<p class="item-total"><spring:message code="item.total.count" arguments="${paginationInfo.totalRecordCount}" /></p>

		<div class="krds-table-wrap">
			<table class="tbl col data">
				<colgroup>
					<col style="width: 5%;">
					<col style="width: 7%;">
					<col style="width: 11%;">
					<col>
					<col style="width: 8%;">
					<col style="width: 9%;">
					<col style="width: 12%;">
					<col style="width: 6%;">
					<col style="width: 10%;">
					<col style="width: 11%;">
				</colgroup>
				<thead>
					<tr>
						<th scope="col" class="text-center">No</th>
						<th scope="col" class="text-center"><spring:message code="title.item.image" /></th>
						<th scope="col" class="text-center"><spring:message code="title.item.itemId" /></th>
						<th scope="col" class="text-center"><spring:message code="title.item.itemName" /></th>
						<th scope="col" class="text-center"><spring:message code="title.item.categoryCode" /></th>
						<th scope="col" class="text-center"><spring:message code="title.item.price" /></th>
						<th scope="col" class="text-center"><spring:message code="title.item.salePrice" /></th>
						<th scope="col" class="text-center"><spring:message code="title.item.stockQty" /></th>
						<th scope="col" class="text-center"><spring:message code="title.item.statusCode" /></th>
						<th scope="col" class="text-center"><spring:message code="title.item.regDate" /></th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty resultList}">
							<c:forEach var="result" items="${resultList}" varStatus="status">
								<c:url var="imgSrc" value="${empty result.imageUrl ? '/img/item/category-'.concat(result.categoryCode).concat('.svg') : result.imageUrl}"/>
								<tr>
									<td class="text-center"><c:out value="${paginationInfo.totalRecordCount + 1 - ((itemVO.pageIndex - 1) * paginationInfo.recordCountPerPage + status.count)}"/></td>
									<td class="text-center"><img class="item-thumb" src="<c:out value='${imgSrc}'/>" alt="" loading="lazy" /></td>
									<td class="text-center"><a href="#" onclick="fn_egov_select('<c:out value="${result.itemId}"/>'); return false;"><c:out value="${result.itemId}"/></a></td>
									<td><a href="#" onclick="fn_egov_select('<c:out value="${result.itemId}"/>'); return false;"><c:out value="${result.itemName}"/></a></td>
									<td class="text-center"><spring:message code="item.category.${result.categoryCode}" text="${result.categoryCode}" /></td>
									<td class="text-right"><fmt:formatNumber value="${result.price}" pattern="#,##0"/></td>
									<td class="text-right item-nowrap">
										<c:choose>
											<c:when test="${result.onSale}">
												<fmt:formatNumber value="${result.salePrice}" pattern="#,##0"/>
												<span class="item-discount">-${result.discountRate}%</span>
											</c:when>
											<c:otherwise>-</c:otherwise>
										</c:choose>
									</td>
									<td class="text-right"><fmt:formatNumber value="${result.stockQty}" pattern="#,##0"/></td>
									<td class="text-center item-nowrap">
										<c:choose>
											<c:when test="${result.statusCode == 'SALE'}"><span class="krds-badge bg-light-success"><spring:message code="item.status.SALE" /></span></c:when>
											<c:when test="${result.statusCode == 'SOLDOUT'}"><span class="krds-badge bg-light-warning"><spring:message code="item.status.SOLDOUT" /></span></c:when>
											<c:otherwise><span class="krds-badge bg-light-gray"><spring:message code="item.status.${result.statusCode}" text="${result.statusCode}" /></span></c:otherwise>
										</c:choose>
									</td>
									<td class="text-center item-nowrap"><fmt:formatDate value="${result.regDate}" pattern="yyyy-MM-dd"/></td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td class="text-center" colspan="10"><spring:message code="info.nodata.msg" /></td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>

		<div id="paging" class="krds-pagination w-page">
			<ui:pagination paginationInfo="${paginationInfo}" type="krds" jsFunction="fn_egov_link_page" />
		</div>
	</div>

</div>
</body>
</html>

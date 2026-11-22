<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
/**
* @Class Name : egovItemRegister.jsp
* @Description : 상품 등록·수정 화면
* @Modification Information
*
*   수정일         수정자                   수정내용
*  -------    --------    ---------------------------
*  2026.11.22				최초 생성
*/
%>
<c:set var="navActive" value="item"/>
<c:set var="registerFlag" value="${empty itemVO.itemId ? 'create' : 'modify'}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="${registerFlag == 'create' ? 'title.item.register' : 'title.item.modify'}" /></title>

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/component/output.css'/>" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/egovframe.css'/>" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/item.css'/>" />
	<script type="text/javascript" src="<c:url value='/js/jquery.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/egovframework/EgovValidation.js'/>" defer></script>

	<script type="text/javascript">
	const contextPath = "<c:out value='${pageContext.request.contextPath}'/>";
	const categoryImageBase = contextPath + "/img/item/category-";

	$(function() {
		const $desc = $('#description');
		$('#textCount').text($desc.val().length);
		$desc.on('input', function() {
			$('#textCount').text($(this).val().length);
		});
		$('#imageUrl, #categoryCode').on('input change', updatePreview);
		updatePreview();
	});

	/* 입력한 이미지 URL 또는 카테고리 기본 이미지를 미리 보여준다. */
	function updatePreview() {
		const url = $.trim($('#imageUrl').val());
		let src = categoryImageBase + $('#categoryCode').val() + '.svg';
		if (/^https?:\/\//.test(url)) {
			src = url;
		} else if (/^\/(?!\/)/.test(url)) {
			src = contextPath + url;
		}
		$('#imagePreview').attr('src', src);
	}

	function itemList() {
		const frm = document.detailForm;
		frm.action = "<c:url value='/item/egovItemList.do'/>";
		frm.method = 'get';
		frm.submit();
	}

	function itemSave(url, confirmMsg) {
		const frm = document.detailForm;
		if (!validateItemVO(frm)) {
			return;
		}
		if (confirm(confirmMsg)) {
			frm.action = url;
			frm.method = 'post';
			frm.submit();
		}
	}

	function itemAdd() {
		itemSave("<c:url value='/item/addItem.do'/>", "<spring:message code='add.confirm' javaScriptEscape='true'/>");
	}

	function itemUpdate() {
		itemSave("<c:url value='/item/updateItem.do'/>", "<spring:message code='modify.confirm' javaScriptEscape='true'/>");
	}

	function itemDelete() {
		if (confirm("<spring:message code='delete.confirm' javaScriptEscape='true'/>")) {
			const frm = document.detailForm;
			frm.action = "<c:url value='/item/deleteItem.do'/>";
			frm.method = 'post';
			frm.submit();
		}
	}

	function itemReset() {
		document.detailForm.reset();
		$('#textCount').text($('#description').val().length);
		updatePreview();
	}
	</script>
</head>

<body>
<div id="container" class="inner">

	<%@ include file="itemNav.jspf" %>

	<h2 class="heading-large"><spring:message code="${registerFlag == 'create' ? 'title.item.register' : 'title.item.modify'}" /></h2>

	<form:form id="detailForm" name="detailForm" modelAttribute="itemVO">
		<form:hidden path="searchCondition" />
		<form:hidden path="searchKeyword" />
		<form:hidden path="searchCategory" />
		<form:hidden path="searchStatus" />
		<form:hidden path="sortOrder" />
		<form:hidden path="pageIndex" />

		<div class="conts-wrap">
			<div class="item-form-layout">
				<div class="item-form-preview">
					<img id="imagePreview" src="<c:url value='/img/item/category-FASHION.svg'/>" alt="" />
				</div>

				<div class="fieldset input-form item-form-fields">
					<c:if test="${registerFlag == 'modify'}">
					<div class="form-group">
						<div class="form-tit"><label for="itemId"><spring:message code="title.item.itemId" /></label></div>
						<div class="form-conts">
							<form:input path="itemId" maxlength="16" readonly="true" cssClass="krds-input"/>
						</div>
					</div>
					</c:if>

					<div class="form-group">
						<div class="form-tit"><label for="itemName"><spring:message code="title.item.itemName" /> <span class="required">*</span></label></div>
						<div class="form-conts">
							<spring:message code="item.required.itemName" var="placeholderItemName"/>
							<form:input path="itemName" maxlength="100" cssClass="krds-input" placeholder="${placeholderItemName}" />
							<form:errors path="itemName" cssClass="error-message" />
						</div>
					</div>

					<div class="item-form-row">
						<div class="form-group">
							<div class="form-tit"><label for="categoryCode"><spring:message code="title.item.categoryCode" /> <span class="required">*</span></label></div>
							<div class="form-conts">
								<form:select path="categoryCode" cssClass="krds-form-select">
									<c:forEach var="category" items="${categoryList}">
										<form:option value="${category}"><spring:message code="item.category.${category}" /></form:option>
									</c:forEach>
								</form:select>
								<form:errors path="categoryCode" cssClass="error-message" />
							</div>
						</div>

						<div class="form-group">
							<div class="form-tit"><label for="statusCode"><spring:message code="title.item.statusCode" /> <span class="required">*</span></label></div>
							<div class="form-conts">
								<form:select path="statusCode" cssClass="krds-form-select">
									<c:forEach var="itemStatus" items="${statusList}">
										<form:option value="${itemStatus}"><spring:message code="item.status.${itemStatus}" /></form:option>
									</c:forEach>
								</form:select>
								<form:errors path="statusCode" cssClass="error-message" />
							</div>
						</div>
					</div>

					<div class="item-form-row">
						<div class="form-group">
							<div class="form-tit"><label for="price"><spring:message code="title.item.price" /> <span class="required">*</span></label></div>
							<div class="form-conts">
								<form:input path="price" type="number" min="0" max="100000000" step="1" cssClass="krds-input" />
								<form:errors path="price" cssClass="error-message" />
							</div>
						</div>

						<div class="form-group">
							<div class="form-tit"><label for="salePrice"><spring:message code="title.item.salePrice" /></label></div>
							<div class="form-conts">
								<spring:message code="item.hint.salePrice" var="placeholderSalePrice"/>
								<form:input path="salePrice" type="number" min="0" max="100000000" step="1" cssClass="krds-input" placeholder="${placeholderSalePrice}" />
								<form:errors path="salePrice" cssClass="error-message" />
							</div>
						</div>

						<div class="form-group">
							<div class="form-tit"><label for="stockQty"><spring:message code="title.item.stockQty" /> <span class="required">*</span></label></div>
							<div class="form-conts">
								<form:input path="stockQty" type="number" min="0" max="1000000" step="1" cssClass="krds-input" />
								<form:errors path="stockQty" cssClass="error-message" />
							</div>
						</div>
					</div>
					<p class="item-hint"><spring:message code="item.hint.soldout" /></p>

					<div class="form-group">
						<div class="form-tit"><label for="imageUrl"><spring:message code="title.item.imageUrl" /></label></div>
						<div class="form-conts">
							<form:input path="imageUrl" maxlength="500" cssClass="krds-input" placeholder="https://" />
							<p class="item-hint"><spring:message code="item.hint.imageUrl" /></p>
							<form:errors path="imageUrl" cssClass="error-message" />
						</div>
					</div>

					<div class="form-group">
						<div class="form-tit"><label for="description"><spring:message code="title.item.description" /></label></div>
						<div class="form-conts">
							<div class="textarea-wrap">
								<form:textarea path="description" cssClass="krds-input" maxlength="2000" rows="6" />
								<p class="textarea-count">
									<span id="textCount" class="text-primary">0</span>
									<span class="text-num">/2000</span>
								</p>
							</div>
							<form:errors path="description" cssClass="error-message" />
						</div>
					</div>

					<div class="form-group">
						<div class="form-tit"><label for="regUser"><spring:message code="title.item.regUser" /> <span class="required">*</span></label></div>
						<div class="form-conts">
							<spring:message code="item.required.regUser" var="placeholderRegUser"/>
							<c:choose>
								<c:when test="${registerFlag == 'modify'}">
									<form:input path="regUser" maxlength="10" readonly="true" cssClass="krds-input" />
								</c:when>
								<c:otherwise>
									<form:input path="regUser" maxlength="10" cssClass="krds-input" placeholder="${placeholderRegUser}" />
								</c:otherwise>
							</c:choose>
							<form:errors path="regUser" cssClass="error-message" />
						</div>
					</div>

					<c:if test="${registerFlag == 'modify'}">
					<p class="item-hint">
						<spring:message code="title.item.regDate" />: <fmt:formatDate value="${itemVO.regDate}" pattern="yyyy-MM-dd HH:mm"/>
						<c:if test="${not empty itemVO.updDate}">
							&nbsp;|&nbsp; <spring:message code="title.item.updDate" />: <fmt:formatDate value="${itemVO.updDate}" pattern="yyyy-MM-dd HH:mm"/>
						</c:if>
					</p>
					</c:if>
				</div>
			</div>

			<div class="page-btn-wrap">
				<div class="btn-wrap">
					<button type="button" class="krds-btn medium secondary" onclick="itemList()"><spring:message code="button.list" /></button>
					<button type="button" class="krds-btn medium tertiary" onclick="itemReset()"><spring:message code="button.reset" /></button>
				</div>
				<div class="btn-wrap">
					<c:choose>
						<c:when test="${registerFlag == 'modify'}">
							<c:url var="shopDetailUrl" value="/shop/itemDetail.do"><c:param name="itemId" value="${itemVO.itemId}"/></c:url>
							<c:if test="${itemVO.statusCode != 'STOP'}">
								<a class="krds-btn medium secondary" href="<c:out value='${shopDetailUrl}'/>" target="_blank" rel="noopener"><spring:message code="button.shop" /></a>
							</c:if>
							<button type="button" class="krds-btn medium" onclick="itemUpdate()"><spring:message code="button.modify" /></button>
							<button type="button" class="krds-btn medium danger" onclick="itemDelete()"><spring:message code="button.delete" /></button>
						</c:when>
						<c:otherwise>
							<button type="button" class="krds-btn medium" onclick="itemAdd()"><spring:message code="button.create" /></button>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</form:form>

</div>
</body>
</html>

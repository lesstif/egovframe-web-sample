# 표준프레임워크 심플 게시판

[![eGovFrame](https://img.shields.io/badge/eGovFrame-5.0.1-134F8C?labelColor=white&logo=data:image/svg%2Bxml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxNzMuMjgyIDE3My4yODIiPjxwYXRoIGZpbGw9IiNmZmYiIGQ9Ik0xNzMuMjgyIDg2LjY1YzAgNDcuODQ0LTM4Ljc5NCA4Ni42MzItODYuNjQ2IDg2LjYzMkMzOC43OTkgMTczLjI4MiAwIDEzNC40OTQgMCA4Ni42NSAwIDM4Ljc4OCAzOC43OTkgMCA4Ni42MzYgMGM0Ny44NTIgMCA4Ni42NDYgMzguNzg4IDg2LjY0NiA4Ni42NSIvPjxwYXRoIGZpbGw9IiMwMDM3NjQiIGQ9Ik0xMjcuMzg5IDgwLjU5OGMtMTMuNzkxLTkuMzY1LTMxLjQzOS01LjU0Mi00MC43MTcgOC41MzMtNy43MiAxMS43NjctMTkuNDA1IDEzLjIzNS0yMy45MDYgMTMuMjM1LTE0Ljc1MSAwLTI0LjgyNC0xMC4zNjctMjcuODE4LTIxLjA5NWgtLjAxYy0uMDM5LS4xMDgtLjA1OS0uMi0uMDktLjMwNy0uMDI1LS4xMi0uMDU2LS4yMzMtLjA4OS0uMzY2LTEuMTc3LTQuNDY3LTEuNDY3LTYuNjA5LTEuNDY3LTExLjM2OCAwLTI1LjY1IDI2LjMyMS01NC4yMTMgNjQuMjE1LTU0LjIxMyAzOC44MjkgMCA2MS4wNSAyOS41NDUgNjYuNzggNDUuOTc5LS4xMDctLjI5NS0uMjA5LS41ODEtLjI5MS0uODc3LTExLjAxNS0zMi4xMjUtNDEuNDc0LTU1LjIxNi03Ny4zNTctNTUuMjE2LTQ1LjEzIDAtODEuNzI5IDM2LjU5LTgxLjcyOSA4MS43MzkgMCA0MC4zNTEgMjkuMTA4IDc0Ljg5MSA2OS40NzkgNzQuODkxIDMyLjE5NyAwIDUzLjg0Mi0xOC4wNTIgNjMuNzU3LTQyLjkzIDUuNDUtMTMuNjE0IDEuNTk1LTI5LjYwNS0xMC43NTctMzguMDA1Ii8%2BPHBhdGggZmlsbD0iI2U0MDMyZSIgZD0iTTE2NC43ODggNjIuNTg5Yy00Ljc3Ny0xNi4wMjYtMjcuMTUzLTQ3LjU3MS02Ny4yODItNDcuNTcxLTM3Ljg5NCAwLTY0LjIxNCAyOC41NjMtNjQuMjE0IDU0LjIxMiAwIDQuNzU5LjI5IDYuOTAxIDEuNDY2IDExLjM2OC0uNDg5LTEuOTUxLS43NC0zLjkwOC0uNzQtNS44MjMgMC0yNi43MjEgMjYuNzQxLTQ1LjIyNyA1NC4yNDgtNDUuMjI3IDM3LjIxOCAwIDY3LjM4OCAzMC4xNzQgNjcuMzg4IDY3LjM5IDAgMjkuMTczLTE2Ljc4NSA1NC40MzctNDEuMTc5IDY2LjU2NXYuMDIzYzMxLjQ1NS0xMS4zOTEgNTMuOTA4LTQxLjUxMiA1My45MDgtNzYuODg0IDAtOC4zNzgtMS4xMjctMTUuNzU5LTMuNTk1LTI0LjA1MyIvPjwvc3ZnPg%3D%3D)](https://www.egovframe.go.kr)
[![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![javascript](https://img.shields.io/badge/javascript-F7DF1E?logo=javascript&logoColor=black)](https://developer.mozilla.org/docs/Web/JavaScript)
[![Spring Framework](https://img.shields.io/badge/Spring%20Framework-6.2.11-F2F4F9?logo=spring)](https://spring.io/projects/spring-framework)
[![maven](https://img.shields.io/badge/Maven-C71A36?logo=apache-maven&logoColor=white)](https://maven.apache.org/)  
![workflow](https://github.com/eGovFramework/egovframe-web-sample/actions/workflows/maven.yml/badge.svg)

※ 본 프로젝트는 Spring 기반의 기본 게시판 예제 소스 코드입니다.

## 환경 설정

| 항목 | 버전 |
| :--------- | :------ |
| JDK | 17 |
| Jakarta EE | 9 |
| Servlet | 5.0 |
| Spring Framework | 6.2.11 |

## 참조 화면

### 목록 화면

![list](https://github.com/user-attachments/assets/6814f4df-6266-4f22-8cde-01acb308c022)

### 게시글 등록 화면

![regist](https://github.com/user-attachments/assets/b58cd205-131b-409f-9de8-62a83bd8029c)

## 상품관리(쇼핑몰) 예제

게시판과 같은 계층 구조(VO → Mapper → Service → Controller → JSP)로 만든 상품관리 예제가 함께 들어 있다. 데이터는 `db/itemdb.sql` 의 HSQLDB 메모리 테이블(ITEM)에 시드된다.

| 화면 | URL | 내용 |
| :--- | :--- | :--- |
| 쇼핑몰 상품 목록 | `/shop/itemList.do` | 카테고리 탭, 검색, 정렬, 할인율·품절 표시 (판매중지 상품은 노출하지 않음) |
| 쇼핑몰 상품 상세 | `/shop/itemDetail.do?itemId=ITEM-00001` | 가격, 재고, 설명 |
| 상품관리 목록 | `/item/egovItemList.do` | 카테고리·판매상태 필터, 상품명/ID 검색, 정렬, 페이징 |
| 상품 등록·수정·삭제 | 상품관리 목록에서 진입 | 할인가 ≤ 정가 검증, 재고 0 인 판매중 상품은 자동 품절 |

## 프로젝트 실행

1. eclipse 하단의 Servers 탭을 클릭하고, 마우스 우클릭하여 **New > Server** 를 선택하여 서버를 설치한다.

2. 실행할 프로젝트를 마우스 우클릭하고 **Run As > Run on Server** 를 선택한다.

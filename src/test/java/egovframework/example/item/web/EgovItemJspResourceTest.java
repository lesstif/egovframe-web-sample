package egovframework.example.item.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import egovframework.example.item.service.ItemCategory;
import egovframework.example.item.service.ItemStatus;

/**
 * [상품관리] JSP 정적 검사: 폼 maxlength 와 컬럼폭, 메시지 코드, 정적 자원
 */
class EgovItemJspResourceTest {

	private static final Path JSP_DIR = Path.of("src/main/webapp/WEB-INF/jsp/egovframework/example/item");

	private static final Path REGISTER_JSP = JSP_DIR.resolve("egovItemRegister.jsp");

	private static final Path ITEM_DDL = Path.of("src/main/resources/db/itemdb.sql");

	private static final Pattern DDL_COLUMN = Pattern.compile("([A-Z_]+) VARCHAR\\((\\d+)\\)");

	private static final Pattern FORM_FIELD = Pattern.compile("path=\"(\\w+)\"[^>]*?maxlength=\"(\\d+)\"",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern MESSAGE_CODE = Pattern.compile("<spring:message[^>]*code=['\"]([^'\"$]+)['\"]");

	private static final Pattern STATIC_URL = Pattern.compile("<c:url[^>]*value=['\"](/[^'\"$]+\\.(?!do['\"])\\w+)['\"]");

	private static final List<Locale> LOCALES = List.of(Locale.KOREAN, Locale.ENGLISH);

	private static String read(Path path) {
		try {
			return Files.readString(path, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static List<Path> itemViews() {
		try (Stream<Path> files = Files.list(JSP_DIR)) {
			return files.filter(p -> p.toString().endsWith(".jsp") || p.toString().endsWith(".jspf")).toList();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Test
	void registerFormMaxLengthFitsColumns() {
		final Map<String, Integer> widths = new LinkedHashMap<>();
		final Matcher column = DDL_COLUMN.matcher(read(ITEM_DDL));
		while (column.find()) {
			widths.put(column.group(1), Integer.parseInt(column.group(2)));
		}

		final List<String> checked = new ArrayList<>();
		final List<String> over = new ArrayList<>();
		final Matcher field = FORM_FIELD.matcher(read(REGISTER_JSP));
		while (field.find()) {
			final String columnName = field.group(1).replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase(Locale.ROOT);
			final Integer width = widths.get(columnName);
			if (width == null) {
				continue;
			}
			checked.add(columnName);
			if (Integer.parseInt(field.group(2)) > width) {
				over.add(field.group(1) + " maxlength=" + field.group(2) + " > VARCHAR(" + width + ")");
			}
		}

		assertEquals(List.of("ITEM_ID", "ITEM_NAME", "IMAGE_URL", "DESCRIPTION", "REG_USER", "REG_USER"), checked);
		assertTrue(over.isEmpty(), over.toString());
	}

	@Test
	void messageCodesAndStaticResourcesExist() {
		final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/egovframework/message/message-common");
		messageSource.setFallbackToSystemLocale(false);

		final List<String> codes = new ArrayList<>();
		final List<String> missing = new ArrayList<>();
		for (final Path view : itemViews()) {
			final String source = read(view);
			final Matcher m = MESSAGE_CODE.matcher(source);
			while (m.find()) {
				codes.add(m.group(1));
			}
			final Matcher urls = STATIC_URL.matcher(source);
			while (urls.find()) {
				if (!Files.isRegularFile(Path.of("src/main/webapp", urls.group(1)))) {
					missing.add(view.getFileName() + " : " + urls.group(1));
				}
			}
		}
		for (final ItemCategory category : ItemCategory.values()) {
			codes.add("item.category." + category);
			if (!Files.isRegularFile(Path.of("src/main/webapp/img/item/category-" + category + ".svg"))) {
				missing.add("category image " + category);
			}
		}
		for (final ItemStatus itemStatus : ItemStatus.values()) {
			codes.add("item.status." + itemStatus);
		}
		for (final String sort : List.of("latest", "priceAsc", "priceDesc", "name")) {
			codes.add("item.sort." + sort);
		}

		for (final String code : codes) {
			for (final Locale locale : LOCALES) {
				try {
					messageSource.getMessage(code, null, locale);
				} catch (final NoSuchMessageException e) {
					missing.add(code + " (" + locale + ")");
				}
			}
		}

		assertTrue(codes.size() > 30, "codes=" + codes.size());
		assertTrue(missing.isEmpty(), missing.toString());
	}

}

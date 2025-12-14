# Stroke Order GIFs

Thư mục này chứa các GIF hiển thị thứ tự nét viết của chữ Nhật.

## Cấu trúc thư mục

```
strokes/
├── hiragana/    # 46 ký tự Hiragana
├── katakana/    # 46 ký tự Katakana
└── kanji/       # Các Kanji thường dùng
```

## Quy tắc đặt tên file

### Hiragana (46 ký tự)
Sử dụng romaji: `a.gif`, `i.gif`, `u.gif`, `e.gif`, `o.gif`...

| Hàng | Files |
|------|-------|
| あ行 | a.gif, i.gif, u.gif, e.gif, o.gif |
| か行 | ka.gif, ki.gif, ku.gif, ke.gif, ko.gif |
| さ行 | sa.gif, shi.gif, su.gif, se.gif, so.gif |
| た行 | ta.gif, chi.gif, tsu.gif, te.gif, to.gif |
| な行 | na.gif, ni.gif, nu.gif, ne.gif, no.gif |
| は行 | ha.gif, hi.gif, fu.gif, he.gif, ho.gif |
| ま行 | ma.gif, mi.gif, mu.gif, me.gif, mo.gif |
| や行 | ya.gif, yu.gif, yo.gif |
| ら行 | ra.gif, ri.gif, ru.gif, re.gif, ro.gif |
| わ行 | wa.gif, wo.gif, n.gif |

### Katakana (46 ký tự)
Cùng quy tắc với Hiragana, đặt trong thư mục `katakana/`

### Kanji
Sử dụng chính ký tự Kanji: `日.gif`, `本.gif`, `語.gif`...

## Nguồn tải GIF miễn phí

1. **KanjiVG** - https://kanjivg.tagaini.net/
   - SVG strokes có thể convert sang GIF
   - Open source, miễn phí

2. **Jisho.org** - https://jisho.org/
   - Có stroke order cho mỗi ký tự
   - Cần capture thành GIF

3. **KanjiAlive** - https://kanjialive.com/
   - GIF animations có sẵn
   - API miễn phí cho educational use

4. **Wikimedia Commons**
   - Tìm kiếm "stroke order animation"
   - Nhiều GIF miễn phí

## Kích thước đề xuất

- **Kích thước**: 120x120 px hoặc 150x150 px
- **Định dạng**: GIF (hỗ trợ animation)
- **Frame rate**: 5-10 fps
- **Màu sắc**: Nền trắng, nét đen

## Cách sử dụng trong code

```kotlin
// Load GIF từ assets
val context = LocalContext.current
val inputStream = context.assets.open("strokes/hiragana/a.gif")

// Hoặc sử dụng Coil
AsyncImage(
    model = "file:///android_asset/strokes/hiragana/a.gif",
    contentDescription = "Stroke order for あ"
)
```

## Danh sách file cần có

### Hiragana (46 files)
```
a.gif, i.gif, u.gif, e.gif, o.gif,
ka.gif, ki.gif, ku.gif, ke.gif, ko.gif,
sa.gif, shi.gif, su.gif, se.gif, so.gif,
ta.gif, chi.gif, tsu.gif, te.gif, to.gif,
na.gif, ni.gif, nu.gif, ne.gif, no.gif,
ha.gif, hi.gif, fu.gif, he.gif, ho.gif,
ma.gif, mi.gif, mu.gif, me.gif, mo.gif,
ya.gif, yu.gif, yo.gif,
ra.gif, ri.gif, ru.gif, re.gif, ro.gif,
wa.gif, wo.gif, n.gif
```

### Katakana (46 files)
Cùng danh sách như Hiragana

### Kanji N5 (khoảng 80 ký tự)
```
日.gif, 本.gif, 人.gif, 大.gif, 小.gif, 中.gif, 山.gif, 川.gif,
水.gif, 火.gif, 木.gif, 金.gif, 土.gif, 月.gif, 年.gif, 時.gif,
...
```

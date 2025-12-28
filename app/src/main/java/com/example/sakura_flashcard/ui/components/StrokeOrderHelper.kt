package com.example.sakura_flashcard.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sakura_flashcard.R
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

/**
 * Mapping từ Hiragana character sang resource ID của hình ảnh stroke order
 */
object StrokeOrderHelper {
    
    private val hiraganaToRomaji = mapOf(
        "あ" to "a", "い" to "i", "う" to "u", "え" to "e", "お" to "o",
        "か" to "ka", "き" to "ki", "く" to "ku", "け" to "ke", "こ" to "ko",
        "さ" to "sa", "し" to "shi", "す" to "su", "せ" to "se", "そ" to "so",
        "た" to "ta", "ち" to "chi", "つ" to "tsu", "て" to "te", "と" to "to",
        "な" to "na", "に" to "ni", "ぬ" to "nu", "ね" to "ne", "の" to "no",
        "は" to "ha", "ひ" to "hi", "ふ" to "fu", "へ" to "he", "ほ" to "ho",
        "ま" to "ma", "み" to "mi", "む" to "mu", "め" to "me", "も" to "mo",
        "や" to "ya", "ゆ" to "yu", "よ" to "yo",
        "ら" to "ra", "り" to "ri", "る" to "ru", "れ" to "re", "ろ" to "ro",
        "わ" to "wa", "を" to "wo", "ん" to "n"
    )
    
    private val romajiToResourceId = mapOf(
        "a" to R.drawable.stroke_hiragana_a,
        "i" to R.drawable.stroke_hiragana_i,
        "u" to R.drawable.stroke_hiragana_u,
        "e" to R.drawable.stroke_hiragana_e,
        "o" to R.drawable.stroke_hiragana_o,
        "ka" to R.drawable.stroke_hiragana_ka,
        "ki" to R.drawable.stroke_hiragana_ki,
        "ku" to R.drawable.stroke_hiragana_ku,
        "ke" to R.drawable.stroke_hiragana_ke,
        "ko" to R.drawable.stroke_hiragana_ko,
        "sa" to R.drawable.stroke_hiragana_sa,
        "shi" to R.drawable.stroke_hiragana_shi,
        "su" to R.drawable.stroke_hiragana_su,
        "se" to R.drawable.stroke_hiragana_se,
        "so" to R.drawable.stroke_hiragana_so,
        "ta" to R.drawable.stroke_hiragana_ta,
        "chi" to R.drawable.stroke_hiragana_chi,
        "tsu" to R.drawable.stroke_hiragana_tsu,
        "te" to R.drawable.stroke_hiragana_te,
        "to" to R.drawable.stroke_hiragana_to,
        "na" to R.drawable.stroke_hiragana_na,
        "ni" to R.drawable.stroke_hiragana_ni,
        "nu" to R.drawable.stroke_hiragana_nu,
        "ne" to R.drawable.stroke_hiragana_ne,
        "no" to R.drawable.stroke_hiragana_no,
        "ha" to R.drawable.stroke_hiragana_ha,
        "hi" to R.drawable.stroke_hiragana_hi,
        "fu" to R.drawable.stroke_hiragana_fu,
        "he" to R.drawable.stroke_hiragana_he,
        "ho" to R.drawable.stroke_hiragana_ho,
        "ma" to R.drawable.stroke_hiragana_ma,
        "mi" to R.drawable.stroke_hiragana_mi,
        "mu" to R.drawable.stroke_hiragana_mu,
        "me" to R.drawable.stroke_hiragana_me,
        "mo" to R.drawable.stroke_hiragana_mo,
        "ya" to R.drawable.stroke_hiragana_ya,
        "yu" to R.drawable.stroke_hiragana_yu,
        "yo" to R.drawable.stroke_hiragana_yo,
        "ra" to R.drawable.stroke_hiragana_ra,
        "ri" to R.drawable.stroke_hiragana_ri,
        "ru" to R.drawable.stroke_hiragana_ru,
        "re" to R.drawable.stroke_hiragana_re,
        "ro" to R.drawable.stroke_hiragana_ro,
        "wa" to R.drawable.stroke_hiragana_wa,
        "wo" to R.drawable.stroke_hiragana_wo,
        "n" to R.drawable.stroke_hiragana_n
    )
    
    /**
     * Lấy resource ID của hình ảnh stroke order từ ký tự Hiragana
     * @param hiragana Ký tự Hiragana (ví dụ: "あ")
     * @return Resource ID hoặc null nếu không tìm thấy
     */
    fun getStrokeOrderResource(hiragana: String): Int? {
        val romaji = hiraganaToRomaji[hiragana] ?: return null
        return romajiToResourceId[romaji]
    }
    
    /**
     * Kiểm tra có hình ảnh stroke order cho character này không
     */
    fun hasStrokeOrder(character: String): Boolean {
        return hiraganaToRomaji.containsKey(character)
    }
}

/**
 * Dialog hiển thị hình ảnh hướng dẫn nét viết
 */
@Composable
fun StrokeOrderDialog(
    character: String,
    onDismiss: () -> Unit
) {
    val resourceId = StrokeOrderHelper.getStrokeOrderResource(character)
    
    if (resourceId != null) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✍️ Hướng dẫn viết: $character",
                        style = AppTypography.TitleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.PrimaryLight
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = "Stroke order for $character",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Đã hiểu")
                    }
                }
            }
        }
    }
}

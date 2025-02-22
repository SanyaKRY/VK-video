package kreyer.my.util.vk_video.util.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import kreyer.my.util.vk_video.R

fun ImageView.loadImage(imageUri: String?) {
    val image = imageUri ?: R.drawable.default_image
    Glide.with(context)
        .load(image)
        .placeholder(R.drawable.ic_launcher_foreground)
        .error(R.drawable.ic_launcher_foreground)
        .override(170, 230)
        .into(this)
}

package com.example.myjourney.ui.components

import android.R.attr.id
import android.media.browse.MediaBrowser
import android.view.Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myjourney.R
import com.example.myjourney.ui.theme.MyJourneyTheme

@Composable
fun MediaItem(
    imageRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Image( //All media items will be images
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

//Light theme Preview
@Preview(showBackground = true)
@Composable
fun MediaItemLightPreview() {
    MyJourneyTheme(darkTheme = false) {
        MediaItem(imageRes = R.drawable.image1,
        modifier = Modifier
        )
    }
}
package com.example.staggeredphotogallery


import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.ui.tooling.preview.Preview
import com.example.staggeredphotogallery.ui.theme.StaggeredPhotoGalleryTheme
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StaggeredPhotoGalleryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(context = this)
                }
            }
        }
    }
}


// Photo data class
data class Photo(val title: String, val filename: String)


// Get images from photos.xml
fun loadPhotos(context: Context): List<Photo> {
    val photos = mutableListOf<Photo>()
    val parser = context.resources.getXml(R.xml.photos)


    var title = ""
    var filename = ""


    //loop through XML elements
    while (parser.eventType != XmlPullParser.END_DOCUMENT) {
        when (parser.eventType) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    //get title and filename
                    "title" -> title = parser.nextText()
                    "filename" -> filename = parser.nextText()
                }
            }
            XmlPullParser.END_TAG -> {
                if (parser.name == "photo") {
                    photos.add(Photo(title, filename))
                }
            }
        }
        parser.next() //go to next xml element
    }
    return photos
}


// display the photo gallery
@Composable
fun MainScreen(context: Context) {
    val photos = remember { mutableStateOf(loadPhotos(context)) }
    val listState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }


    Box(modifier = Modifier.fillMaxSize()) {
        //photo grid with two columns
        LazyVerticalGrid(columns = GridCells.Fixed(2), state = listState)
        {
            items(photos.value) { photo ->
                Image(
                    painter = painterResource(id = getDrawableId(photo.filename)),
                    contentDescription = photo.title,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .clickable { selectedPhoto = photo }
                )
            }
        }


        //if a photo is selected
        selectedPhoto?.let {
            //call fun to enlarge it and display on the center of the screen
            EnlargedPhoto(photo = it) { selectedPhoto = null }
        }

    }
}


// center and enlarge image when clicking on a photo
@Composable
fun EnlargedPhoto(photo: Photo, onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = getDrawableId(photo.filename)),
                contentDescription = photo.title,
                modifier = Modifier.size(300.dp)
            )
        }
    }
}


// mapping filenames from photos.xml to drawable resources
fun getDrawableId(filename: String): Int {
    return when (filename) {
        "black_cat" -> R.drawable.black_cat
        "blue_flowers" -> R.drawable.blue_flowers
        "firework" -> R.drawable.firework
        "fireworks" -> R.drawable.fireworks
        "flower" -> R.drawable.flower
        "paint_book" -> R.drawable.paint_book
        "purple_flowers" -> R.drawable.purple_flowers
        "sea" -> R.drawable.sea
        "shells" -> R.drawable.shells
        else -> R.drawable.ic_launcher_foreground
    }
}
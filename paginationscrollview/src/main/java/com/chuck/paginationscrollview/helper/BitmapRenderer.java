/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chuck.paginationscrollview.helper;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Build;

import com.chuck.paginationscrollview.view.Utilities;

public class BitmapRenderer {

     public static final boolean USE_HARDWARE_BITMAP = Utilities.ATLEAST_P;

     public static Bitmap createSoftwareBitmap(int width, int height, Renderer renderer) {
          Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
          renderer.draw(new Canvas(result));
          return result;
     }

     @TargetApi(Build.VERSION_CODES.P)
     public static Bitmap createHardwareBitmap(int width, int height, Renderer renderer) {
          if (!USE_HARDWARE_BITMAP) {
               return createSoftwareBitmap(width, height, renderer);
          }

          Picture picture = new Picture();
          renderer.draw(picture.beginRecording(width, height));
          picture.endRecording();
          return Bitmap.createBitmap(picture);
     }

     /**
      * Interface representing a bitmap draw operation.
      */
     public interface Renderer {
          void draw(Canvas out);
     }
}

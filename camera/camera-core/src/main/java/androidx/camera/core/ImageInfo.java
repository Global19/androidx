/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.camera.core;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

/** Metadata for an image. */
public interface ImageInfo {
    /**
     * Returns the tag of the metadata.
     *
     * @hide
     * */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Nullable
    Object getTag();

    /** Returns the timestamp of the metadata.
     *
     * Details on the timestamp depend on the source providing the image, and the method providing
     * the image contains more documentation.
     *
     * @return the timestamp of the image
     */
    long getTimestamp();
}

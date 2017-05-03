/*
 * Copyright (C) 2016 Square, Inc.
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
package attendance.mll.com.check.http;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class ArrayResponseBodyConverters {
  private ArrayResponseBodyConverters() {
  }

  static final class  ByteArrayResponseBodyConverter implements Converter<ResponseBody, byte[]> {
    static final ByteArrayResponseBodyConverter INSTANCE = new ByteArrayResponseBodyConverter();

    @Override public byte[] convert(ResponseBody value) throws IOException {
      return value.bytes();
    }
  }
}

// Copyright 2011 two forty four a.m. LLC <http://www.twofortyfouram.com>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.yourcompany.yoursetting;

/**
 * Class of constants used by this <i>Locale</i> plug-in.
 */
final class Constants
{
	/**
	 * Private constructor prevents instantiation
	 *
	 * @throws UnsupportedOperationException because this class cannot be instantiated.
	 */
	private Constants()
	{
		throw new UnsupportedOperationException(String.format("%s(): This class is non-instantiable", this.getClass().getSimpleName())); //$NON-NLS-1$
	}

	/**
	 * Log tag for logcat messages
	 */
	static final String LOG_TAG = "Toast"; //$NON-NLS-1$

	/**
	 * Type: {@code boolean}
	 * <p>
	 * SharedPreference key for retrieving a boolean as to whether the license has been agreed to
	 */
	static final String PREFERENCE_BOOLEAN_IS_LICENSE_AGREED = "IS_LICENSE_AGREED"; //$NON-NLS-1$

	/**
	 * Type: {@code String}
	 * <p>
	 * Maps to a {@code String} in the store-and-forward {@code Bundle} {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE}
	 */
	static final String BUNDLE_EXTRA_STRING_MESSAGE = "com.yourcompany.yourapp.extra.MESSAGE"; //$NON-NLS-1$

}
/*******************************************************************************
 * Copyright (c) 2013 - 2014 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.grible.tests;

import org.assertj.core.api.Assertions;
import org.grible.helpers.StringHelper;
import org.testng.annotations.Test;

import java.io.File;

public class StringHelperTest {

    @Test
    public void getFolderPathEmptyTest() {
        Assertions.assertThat(StringHelper.getFolderPath(null)).isEmpty();
    }

    @Test
    public void getFolderPathTest() {
        String actual = "third;second;first";
        String expected = "first" + File.separator + "second" + File.separator + "third";
        Assertions.assertThat(StringHelper.getFolderPath(actual)).isEqualTo(expected);
    }
}

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
package org.grible.tests.integration;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class GribleStartIT extends BaseTest {

    @Test
    public void testHomePageTitle() {
        browser.get("localhost:8123");
        Assertions.assertThat(browser.getTitle()).isEqualTo("First launch - Grible");
    }

    @Test
    public void testHomePageUrl() {
        browser.get("localhost:8123");
        Assertions.assertThat(browser.getCurrentUrl()).contains("firstlaunch");
    }

}

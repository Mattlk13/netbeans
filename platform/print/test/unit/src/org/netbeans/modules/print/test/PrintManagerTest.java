/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.modules.print.test;

import javax.swing.Action;
import javax.swing.JComponent;
import junit.framework.TestCase;
import org.netbeans.api.print.PrintManager;

/**
 * @author Vladimir Yaroslavskiy
 * @version 2008.01.17
 */
public class PrintManagerTest extends TestCase {

    public void testPrintAction() {
        Action action = getPrintAction();
        assertTrue("Print action can't be null", action != null); // NOI18N
    }

    public void testPrintActionProperties() {
        Action action = getPrintAction();
        checkProperty(action, Action.SHORT_DESCRIPTION);
        checkProperty(action, Action.SHORT_DESCRIPTION);
        checkProperty(action, Action.SMALL_ICON);
    }

    private Action getPrintAction() {
        return PrintManager.printAction((JComponent) null);
    }

    private void checkProperty(Action action, String property) {
        assertTrue("Print action." + property + " can't be null", action.getValue(property) != null); // NOI18N
    }
}

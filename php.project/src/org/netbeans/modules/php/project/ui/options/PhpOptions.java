/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.project.ui.options;

import java.io.IOException;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.PhpVersion;
import org.netbeans.modules.php.project.PhpPreferences;
import org.netbeans.modules.php.project.environment.PhpEnvironment;
import org.netbeans.modules.php.project.ui.customizer.PhpProjectProperties;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.util.Exceptions;

/**
 * Helper class to get actual PHP properties like debugger port etc.
 * Use {@link #getInstance()} to get class instance.
 * @author Tomas Mysik
 * @since 1.2
 */
public final class PhpOptions {
    // Do not change arbitrary - consult with layer's folder OptionsExport
    // Path to Preferences node for storing these preferences
    private static final String PREFERENCES_PATH = "general"; // NOI18N

    // these constants are used in API javadoc so therefore public modifier
    public static final int DEFAULT_DEBUGGER_PORT = 9000;
    public static final String DEFAULT_DEBUGGER_SESSION_ID = "netbeans-xdebug"; // NOI18N
    public static final int DEFAULT_DEBUGGER_MAX_DATA_LENGTH = 2048;
    public static final int DEFAULT_DEBUGGER_MAX_STRUCTURES_DEPTH = 3;
    public static final int DEFAULT_DEBUGGER_MAX_CHILDREN = 30;
    public static final boolean DEFAULT_DEBUGGER_SHOW_URLS = false;
    public static final boolean DEFAULT_DEBUGGER_SHOW_CONSOLE = false;
    public static final boolean DEFAULT_DEBUGGER_STOP_AT_FIRST_LINE = true;
    public static final boolean DEFAULT_DEBUGGER_WATCHES_AND_EVAL = false;
    public static final boolean DEFAULT_ANNOTATIONS_RESOLVE_DEPRECATED_ELEMENTS = false;
    public static final boolean DEFAULT_ANNOTATIONS_UNKNOWN_ANNOTATIONS_AS_TYPE_ANNOTATIONS = false;

    // php cli
    public static final String PHP_INTERPRETER = "phpInterpreter"; // NOI18N
    public static final String PHP_OPEN_IN_OUTPUT = "phpOpenInOutput"; // NOI18N
    public static final String PHP_OPEN_IN_BROWSER = "phpOpenInBrowser"; // NOI18N
    public static final String PHP_OPEN_IN_EDITOR = "phpOpenInEditor"; // NOI18N

    // debugger
    public static final String PHP_DEBUGGER_PORT = "phpDebuggerPort"; // NOI18N
    public static final String PHP_DEBUGGER_SESSION_ID = "phpDebuggerSessionId"; // NOI18N
    public static final String PHP_DEBUGGER_MAX_DATA_LENGTH = "phpDebuggerMaxDataLength"; // NOI18N
    public static final String PHP_DEBUGGER_MAX_STRUCTURES_DEPTH = "phpDebuggerMaxStructuresDepth"; // NOI18N
    public static final String PHP_DEBUGGER_MAX_CHILDREN = "phpDebuggerMaxChildren"; // NOI18N
    public static final String PHP_DEBUGGER_STOP_AT_FIRST_LINE = "phpDebuggerStopAtFirstLine"; // NOI18N
    public static final String PHP_DEBUGGER_WATCHES_AND_EVAL = "phpDebuggerWatchesAndEval"; // NOI18N
    public static final String PHP_DEBUGGER_SHOW_URLS = "phpDebuggerShowUrls"; // NOI18N
    public static final String PHP_DEBUGGER_SHOW_CONSOLE = "phpDebuggerShowConsole"; // NOI18N

    // annotations
    public static final String PHP_ANNOTATIONS_RESOLVE_DEPRECATED_ELEMENTS = "phpAnnottationsResolveDeprecetatedElements"; // NOI18N
    public static final String PHP_ANNOTATIONS_UNKNOWN_ANNOTATIONS_AS_TYPE_ANNOTATIONS = "phpAnnotationsUnknownAnnotationsAsTypeAnnotations"; // NOI18N

    // global include path
    public static final String PHP_GLOBAL_INCLUDE_PATH = "phpGlobalIncludePath"; // NOI18N

    // "hidden" (without any UI and not project specific)
    private static final String DEFAULT_PHP_VERSION = "defaultPhpVersion"; // NOI18N
    private static final String REMOTE_SYNC_SHOW_SUMMARY = "remote.sync.showSummary"; // NOI18N

    private static final PhpOptions INSTANCE = new PhpOptions();

    private volatile boolean phpInterpreterSearched = false;
    private volatile boolean phpGlobalIncludePathEnsured = false;

    private PhpOptions() {
    }

    public static PhpOptions getInstance() {
        return INSTANCE;
    }

    private Preferences getPreferences() {
        return PhpPreferences.getPreferences(true).node(PREFERENCES_PATH);
    }

    public void addPreferenceChangeListener(PreferenceChangeListener preferenceChangeListener) {
        getPreferences().addPreferenceChangeListener(preferenceChangeListener);
    }

    public void removePreferenceChangeListener(PreferenceChangeListener preferenceChangeListener) {
        getPreferences().removePreferenceChangeListener(preferenceChangeListener);
    }

    @CheckForNull
    public synchronized String getPhpInterpreter() {
        String phpInterpreter = getPreferences().get(PHP_INTERPRETER, null);
        if (phpInterpreter == null && !phpInterpreterSearched) {
            phpInterpreterSearched = true;
            phpInterpreter = PhpEnvironment.get().getAnyPhpInterpreter();
            if (phpInterpreter != null) {
                setPhpInterpreter(phpInterpreter);
            }
        }
        return phpInterpreter;
    }

    public void setPhpInterpreter(String phpInterpreter) {
        getPreferences().put(PHP_INTERPRETER, phpInterpreter);
    }

    public boolean isOpenResultInOutputWindow() {
        return getPreferences().getBoolean(PHP_OPEN_IN_OUTPUT, true);
    }

    public void setOpenResultInOutputWindow(boolean openResultInOutputWindow) {
        getPreferences().putBoolean(PHP_OPEN_IN_OUTPUT, openResultInOutputWindow);
    }

    public boolean isOpenResultInBrowser() {
        return getPreferences().getBoolean(PHP_OPEN_IN_BROWSER, false);
    }

    public void setOpenResultInBrowser(boolean openResultInBrowser) {
        getPreferences().putBoolean(PHP_OPEN_IN_BROWSER, openResultInBrowser);
    }

    public boolean isOpenResultInEditor() {
        return getPreferences().getBoolean(PHP_OPEN_IN_EDITOR, false);
    }

    public void setOpenResultInEditor(boolean openResultInEditor) {
        getPreferences().putBoolean(PHP_OPEN_IN_EDITOR, openResultInEditor);
    }

    public int getDebuggerPort() {
        return getPreferences().getInt(PHP_DEBUGGER_PORT, DEFAULT_DEBUGGER_PORT);
    }

    public void setDebuggerPort(int debuggerPort) {
        getPreferences().putInt(PHP_DEBUGGER_PORT, debuggerPort);
    }

    public String getDebuggerSessionId() {
        return getPreferences().get(PHP_DEBUGGER_SESSION_ID, DEFAULT_DEBUGGER_SESSION_ID);
    }

    public void setDebuggerSessionId(String sessionId) {
        getPreferences().put(PHP_DEBUGGER_SESSION_ID, sessionId);
    }

    public int getDebuggerMaxDataLength() {
        return getPreferences().getInt(PHP_DEBUGGER_MAX_DATA_LENGTH, DEFAULT_DEBUGGER_MAX_DATA_LENGTH);
    }

    public void setDebuggerMaxDataLength(int debuggerMaxDataLength) {
        getPreferences().putInt(PHP_DEBUGGER_MAX_DATA_LENGTH, debuggerMaxDataLength);
    }

    public int getDebuggerMaxStructuresDepth() {
        return getPreferences().getInt(PHP_DEBUGGER_MAX_STRUCTURES_DEPTH, DEFAULT_DEBUGGER_MAX_STRUCTURES_DEPTH);
    }

    public void setDebuggerMaxStructuresDepth(int debuggerMaxStructuresDepth) {
        getPreferences().putInt(PHP_DEBUGGER_MAX_STRUCTURES_DEPTH, debuggerMaxStructuresDepth);
    }

    public int getDebuggerMaxChildren() {
        return getPreferences().getInt(PHP_DEBUGGER_MAX_CHILDREN, DEFAULT_DEBUGGER_MAX_CHILDREN);
    }

    public void setDebuggerMaxChildren(int debuggerMaxChildren) {
        getPreferences().putInt(PHP_DEBUGGER_MAX_CHILDREN, debuggerMaxChildren);
    }

    public boolean isDebuggerStoppedAtTheFirstLine() {
        return getPreferences().getBoolean(PHP_DEBUGGER_STOP_AT_FIRST_LINE, DEFAULT_DEBUGGER_STOP_AT_FIRST_LINE);
    }

    public void setDebuggerStoppedAtTheFirstLine(boolean debuggerStoppedAtTheFirstLine) {
        getPreferences().putBoolean(PHP_DEBUGGER_STOP_AT_FIRST_LINE, debuggerStoppedAtTheFirstLine);
    }

    public boolean isDebuggerWatchesAndEval() {
        return getPreferences().getBoolean(PHP_DEBUGGER_WATCHES_AND_EVAL, DEFAULT_DEBUGGER_WATCHES_AND_EVAL);
    }

    public void setDebuggerWatchesAndEval(boolean debuggerWatchesAndEval) {
        getPreferences().putBoolean(PHP_DEBUGGER_WATCHES_AND_EVAL, debuggerWatchesAndEval);
    }

    public boolean isDebuggerShowUrls() {
        return getPreferences().getBoolean(PHP_DEBUGGER_SHOW_URLS, DEFAULT_DEBUGGER_SHOW_URLS);
    }

    public void setDebuggerShowUrls(boolean debuggerShowUrls) {
        getPreferences().putBoolean(PHP_DEBUGGER_SHOW_URLS, debuggerShowUrls);
    }

    public boolean isDebuggerShowConsole() {
        return getPreferences().getBoolean(PHP_DEBUGGER_SHOW_CONSOLE, DEFAULT_DEBUGGER_SHOW_CONSOLE);
    }

    public void setDebuggerShowConsole(boolean debuggerShowConsole) {
        getPreferences().putBoolean(PHP_DEBUGGER_SHOW_CONSOLE, debuggerShowConsole);
    }

    public boolean isAnnotationsResolveDeprecatedElements() {
        return getPreferences().getBoolean(PHP_ANNOTATIONS_RESOLVE_DEPRECATED_ELEMENTS, DEFAULT_ANNOTATIONS_RESOLVE_DEPRECATED_ELEMENTS);
    }

    public void setAnnotationsResolveDeprecatedElements(boolean resolveDeprecatedElements) {
        getPreferences().putBoolean(PHP_ANNOTATIONS_RESOLVE_DEPRECATED_ELEMENTS, resolveDeprecatedElements);
    }

    public boolean isAnnotationsUnknownAnnotationsAsTypeAnnotations() {
        return getPreferences().getBoolean(PHP_ANNOTATIONS_UNKNOWN_ANNOTATIONS_AS_TYPE_ANNOTATIONS, DEFAULT_ANNOTATIONS_UNKNOWN_ANNOTATIONS_AS_TYPE_ANNOTATIONS);
    }

    public void setAnnotationsUnknownAnnotationsAsTypeAnnotations(boolean unknownAnnotationsAsTypeAnnotations) {
        getPreferences().putBoolean(PHP_ANNOTATIONS_UNKNOWN_ANNOTATIONS_AS_TYPE_ANNOTATIONS, unknownAnnotationsAsTypeAnnotations);
    }

    /**
     * @see #getPhpGlobalIncludePathAsArray()
     */
    public String getPhpGlobalIncludePath() {
        // XXX the default value could be improved (OS dependent)
        return getPreferences().get(PHP_GLOBAL_INCLUDE_PATH, ""); // NOI18N
    }

    public String[] getPhpGlobalIncludePathAsArray() {
        return PropertyUtils.tokenizePath(getPhpGlobalIncludePath());
    }

    public void setPhpGlobalIncludePath(String phpGlobalIncludePath) {
        getPreferences().put(PHP_GLOBAL_INCLUDE_PATH, phpGlobalIncludePath);
        // update global ant properties as well (global include path can be used in project's include path)
        EditableProperties globalProperties = PropertyUtils.getGlobalProperties();
        globalProperties.setProperty(PhpProjectProperties.GLOBAL_INCLUDE_PATH, phpGlobalIncludePath);
        try {
            PropertyUtils.putGlobalProperties(globalProperties);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    // #210057
    /**
     * Ensure that the php.global.include.path is written in build.properties so Ant can see it.
     */
    public void ensurePhpGlobalIncludePath() {
        if (phpGlobalIncludePathEnsured) {
            return;
        }
        phpGlobalIncludePathEnsured = true;
        String phpGlobalIncludePath = getPhpGlobalIncludePath();
        if (!phpGlobalIncludePath.equals(PropertyUtils.getGlobalProperties().getProperty(PhpProjectProperties.GLOBAL_INCLUDE_PATH))) {
            setPhpGlobalIncludePath(phpGlobalIncludePath);
        }
    }

    public PhpVersion getDefaultPhpVersion() {
        String defaultPhpVersion = getPreferences().get(DEFAULT_PHP_VERSION, null);
        if (defaultPhpVersion != null) {
            try {
                return PhpVersion.valueOf(defaultPhpVersion);
            } catch (IllegalArgumentException ex) {
                // ignored
            }
        }
        return PhpVersion.getDefault();
    }

    public void setDefaultPhpVersion(PhpVersion phpVersion) {
        getPreferences().put(DEFAULT_PHP_VERSION, phpVersion.name());
    }

    public boolean getRemoteSyncShowSummary() {
        return getPreferences().getBoolean(REMOTE_SYNC_SHOW_SUMMARY, true);
    }

    public void setRemoteSyncShowSummary(boolean showSummary) {
        getPreferences().putBoolean(REMOTE_SYNC_SHOW_SUMMARY, showSummary);
    }

}
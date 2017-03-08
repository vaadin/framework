/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.minitutorials.v70;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class SimpleLoginUI extends UI {

    @Override
    protected void init(VaadinRequest request) {

        /*
         * Create a new instance of the navigator. The navigator will attach
         * itself automatically to this view.
         */
        new Navigator(this, this);

        /*
         * The initial log view where the user can login to the application
         */
        getNavigator().addView(SimpleLoginView.NAME, SimpleLoginView.class);

        /*
         * Add the main view of the application
         */
        getNavigator().addView(SimpleLoginMainView.NAME,
                SimpleLoginMainView.class);

        /*
         * We use a view change handler to ensure the user is always redirected
         * to the login view if the user is not logged in.
         */
        getNavigator().addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {

                // Check if a user has logged in
                boolean isLoggedIn = getSession().getAttribute("user") != null;
                boolean isLoginView = event
                        .getNewView() instanceof SimpleLoginView;

                if (!isLoggedIn && !isLoginView) {
                    // Redirect to login view always if a user has not yet
                    // logged in
                    getNavigator().navigateTo(SimpleLoginView.NAME);
                    return false;

                } else if (isLoggedIn && isLoginView) {
                    // If someone tries to access to login view while logged in,
                    // then cancel
                    return false;
                }

                return true;
            }

        });
    }
}

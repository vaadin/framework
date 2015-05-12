/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.shared.ui.calendar;

import java.io.Serializable;

/**
 * 
 * @since 7.1
 * 
 */
public class DateConstants implements Serializable {

    public static final String ACTION_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String CLIENT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String CLIENT_TIME_FORMAT = "HH-mm";
    public static final long MINUTEINMILLIS = 60 * 1000;
    public static final long HOURINMILLIS = 60 * MINUTEINMILLIS;
    public static final long DAYINMILLIS = 24 * HOURINMILLIS;
    public static final long WEEKINMILLIS = 7 * DAYINMILLIS;

    public static final int DAYINMINUTES = 24 * 60;
    public static final int HOURINMINUTES = 60;

}

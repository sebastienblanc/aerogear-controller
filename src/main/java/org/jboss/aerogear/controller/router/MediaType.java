/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.aerogear.controller.router;

import org.jboss.aerogear.controller.router.rest.JsonResponder;
import org.jboss.aerogear.controller.view.HtmlViewResponder;
import org.jboss.aerogear.controller.view.JspViewResponder;

public class MediaType {

    public static final MediaType HTML = new MediaType("text/html", HtmlViewResponder.class);
    public static final MediaType JSP = new MediaType(HTML.getMediaType(), JspViewResponder.class);
    public static final MediaType JSON = new MediaType("application/json", JsonResponder.class);

    public static final String ANY = "*/*";

    private final String mediaType;
    private final Class<? extends Responder> responderClass;

    public MediaType(final String mediaType, final Class<? extends Responder> responderClass) {
        this.mediaType = mediaType;
        this.responderClass = responderClass;
    }

    public String getMediaType() {
        return mediaType;
    }

    @Override
    public String toString() {
        return "MediaType[type=" + mediaType + ", responderClass=" + responderClass + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mediaType == null) ? 0 : mediaType.hashCode());
        result = prime * result + ((responderClass == null) ? 0 : responderClass.getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MediaType other = (MediaType) obj;
        if (mediaType == null) {
            if (other.mediaType != null) {
                return false;
            }
        } else if (!mediaType.equals(other.mediaType)) {
            return false;
        }
        if (responderClass == null) {
            if (other.responderClass != null) {
                return false;
            }
        } else if (!responderClass.getName().equals(other.responderClass.getName())) {
            return false;
        }
        return true;
    }

}

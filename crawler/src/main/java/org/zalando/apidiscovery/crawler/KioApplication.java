package org.zalando.apidiscovery.crawler;

import lombok.AllArgsConstructor;
import org.zalando.stups.clients.kio.ApplicationBase;

@AllArgsConstructor
public class KioApplication {

    private ApplicationBase app;

    public String getServiceUrl() {
        return app.getServiceUrl().endsWith("/") ? app.getServiceUrl() : app.getServiceUrl() + "/";
    }

    public String getName() {
        return app.getId();
    }



    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof KioApplication)) return false;
        KioApplication that = (KioApplication) object;
        if (this.app == null ^ that.app == null) return false;
        if (this.app == null && that.app == null) return true;
        return this.app.toString().equals(that.app.toString());
    }

    @Override
    public int hashCode() {
        return this.app == null ? 0 : this.app.toString().hashCode();
    }

}

package org.zalando.apidiscovery.storage;

final class ApiDefinitionStatusStatistics {

    private final String status;
    private final Long count;

    public ApiDefinitionStatusStatistics(String status, Long count) {
        this.status = status;
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public Long getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiDefinitionStatusStatistics that = (ApiDefinitionStatusStatistics) o;

        if (!status.equals(that.status)) return false;
        return count.equals(that.count);
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + count.hashCode();
        return result;
    }
}

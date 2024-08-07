package com.cstz.desensitize.logback.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.cstz.desensitize.logback.config.DesensitizeConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;

import java.util.Set;

/**
 * @Author hhsj
 * @Title: ExcludeMarkerFilter
 * @Description:
 * @Date 2024/8/5 11:24
 */
public class ExcludeMarkerFilter extends AbstractMatcherFilter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
        Set<String> excludeMarkers = DesensitizeConfig.getExcludeMarkers();
        if (excludeMarkers == null || excludeMarkers.isEmpty()) {
            return FilterReply.NEUTRAL;
        }
        Marker marker = event.getMarker();

        if (marker == null || StringUtils.isBlank(marker.getName())) {
            return FilterReply.NEUTRAL;
        }
        boolean exclude = excludeMarkers.stream().anyMatch(excludeMarker -> excludeMarker.equals(marker.getName()));
        return exclude ? FilterReply.DENY : FilterReply.NEUTRAL;
    }
}

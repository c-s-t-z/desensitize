package com.cstz.desensitize.logback.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.cstz.desensitize.logback.config.DesensitizeConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;

/**
 * @Author hhsj
 * @Title: INcludeMarkerFilter
 * @Description:
 * @Date 2024/8/5 11:14
 */
public class IncludeMarkerFilter extends AbstractMatcherFilter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        Marker marker = event.getMarker();
        if (marker == null || StringUtils.isBlank(marker.getName())) {
            return FilterReply.DENY;
        }
        boolean include = DesensitizeConfig.getIncludeMarkers().stream().anyMatch(markerTemp -> markerTemp.equals(event.getMarker().getName()));
        return include ? FilterReply.ACCEPT : FilterReply.DENY;
    }
}

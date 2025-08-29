package javaspringdddenvironment.domain;

import the_monitor.application.dto.ErrorReasonDto;

public interface BaseErrorCode {

    public ErrorReasonDto getReason();

    public ErrorReasonDto getReasonHttpStatus();

}
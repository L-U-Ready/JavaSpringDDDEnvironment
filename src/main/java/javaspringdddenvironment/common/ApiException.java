package javaspringdddenvironment.common;

import javaspringdddenvironment.tossautoassetlog.common.ErrorStatus;
import lombok.Getter;
import the_monitor.application.dto.ErrorReasonDto;

@Getter
public class ApiException extends RuntimeException{

    private final javaspringdddenvironment.tossautoassetlog.common.ErrorStatus errorStatus;

    public ApiException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ErrorReasonDto getErrorReason() {
        return this.errorStatus.getReason();
    }

    public ErrorReasonDto getErrorReasonHttpStatus() {
        return this.errorStatus.getReasonHttpStatus();
    }

}

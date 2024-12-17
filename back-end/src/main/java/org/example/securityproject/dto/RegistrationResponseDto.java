package org.example.securityproject.dto;


public class RegistrationResponseDto {
    private String messageResponse;
    private boolean flag;

    public RegistrationResponseDto() {}

    public String getMessageResponse() {
        return messageResponse;
    }

    public void setMessageResponse(String messageResponse) {
        this.messageResponse = messageResponse;
    } 

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
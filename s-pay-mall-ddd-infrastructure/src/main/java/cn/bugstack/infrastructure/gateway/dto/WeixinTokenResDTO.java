package cn.bugstack.infrastructure.gateway.dto;


import lombok.Data;

@Data
public class WeixinTokenResDTO {

    private String access_token;
    private int expires_in;
    private String errcode;
    private String errmsg;
}

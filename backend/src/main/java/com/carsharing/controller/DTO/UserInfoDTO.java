package com.carsharing.controller.DTO;

import com.carsharing.model.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Base64Utils;

import java.io.Serializable;
import java.sql.Date;

@Data
public class UserInfoDTO implements Serializable {

    private String fullname;
    private String telephone;
    private String seriesNumberPassport;
    private String issuedPassport;
    private Date issuedDatePassport;
    private Date birthday;
    private String birthPlace;
    private String seriesNumberLicence;
    private String issuedLicense;
    private Date issuedDateLicense;
    private Date reallyLicense;
    private String category;
    private String photo; //base64

    public UserInfoDTO(UserInfo userInfo) {
        BeanUtils.copyProperties(userInfo, this, "photo");

        if (userInfo.getPhoto() != null)
            this.photo = Base64Utils.encodeToString(userInfo.getPhoto());
    }

    public static void CopyPropFromUserInfoDTOToUserInfo(UserInfoDTO userInfoDTO, UserInfo userInfo) {
        if (userInfo != null) {
            BeanUtils.copyProperties(userInfoDTO, userInfo, "photo");

            if (userInfoDTO.getPhoto() != null)
                userInfo.setPhoto(Base64Utils.decodeFromString(userInfoDTO.getPhoto()));
        }
    }
}

package com.nandy007.web.model

import java.util.Date
import javax.*
import javax.persistence.*

class User {
    /* @Transient
    private String token;*/

    /**
     * @return id
     */
    /**
     * @param id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    /**
     * @return username
     */
    /**
     * @param username
     */
    var username: String? = null

    /**
     * @return password
     */
    /**
     * @param password
     */
    var password: String? = null

    /**
     * @return nick_name
     */
    /**
     * @param nickName
     */
    @Column(name = "nick_name")
    var nickName: String? = null

    /**
     * @return sex
     */
    /**
     * @param sex
     */
    var sex: Int? = null

    /**
     * @return register_date
     */
    /**
     * @param registerDate
     */
    @Column(name = "register_date")
    var registerDate: Date? = null

    @Column(name = "last_password_reset_date")
    var lastPasswordResetDate: Date? = null
}
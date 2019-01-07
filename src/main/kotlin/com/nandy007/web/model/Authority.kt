package com.nandy007.web.model

import javax.*
import javax.persistence.*

class Authority {
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
     * @return name
     */
    /**
     * @param name
     */
    var name: String? = null
}
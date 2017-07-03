package com.example.diksha.blogs;

/**
 * Created by diksha on 28/6/17.
 */

public class DataStashSchema {

    /**
     *  //
     *      Admin/
     *         email - Id
     *
     *      Members/
     *         email - Id
     *
     *      Blog/
     *         VisibleToAll/
     *                  Id1 - BlogObject
     *                  Id2 - BlogObject
     *         Id of Member 1/
     *                  Id1 - BlogObject
     *                  Id2 - BlogObject
     *         Id of Member 2/
     *                   .
     *                   .
     *                   .
     *         Id of Member N/
     *                   .
     *                   .
     *                   .
     *         UnApprovedBlogs/         ->(VisibleToAdmins)
     *                 Id of Member1/
     *                      ID1 - BlogObject
     *                      ID2 - BlogObject
     *                 Id of Member2/
     *                      ID1 - BO
     *                      ID2 - BO
     *                    .
     *                    .
     *                    .
     *
     */
}
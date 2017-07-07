package com.example.diksha.blogs;

/**
 * Created by diksha on 28/6/17.
 */

public class DataStashSchema {

    /**
     *  /BlogObject -
     *          * title of blog
     *          * description of blog
     *          * name of the blogger
     *          * unique id of the blogger
     *          * photoUrl of the image
     *          * approval
     *          * unique id of the blog
     *
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
     *                  Id3 - BlogObject
     *                  Id4 - BlogObject
     *         Id of Member 2/
     *                   .
     *                   .
     *                   .
     *         Id of Member N/
     *                   .
     *                   .
     *                   .
     *         UnApprovedBlogs/         ->(VisibleToAdmins)
     *                  ID1 - BlogObject
     *                  ID2 - BlogObject
     *                    .
     *                    .
     *         EditingLocks/
     *                  BlogObjectId1/
     *                          lock - T/F
     *                          blockerId - id Of Person Changing the blog
     *                  BlogObjectId2/
     *                          lock - T/F
     *                          blockerId - id Of Person Changing the blog
     *
     *                     .
     *                     .
     *                     .
     *
     */
}
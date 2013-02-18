package org.lloyd.RedditScraper



import org.junit.*
import grails.test.mixin.*

@TestFor(SubredditController)
@Mock(Subreddit)
class SubredditControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/subreddit/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.subredditInstanceList.size() == 0
        assert model.subredditInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.subredditInstance != null
    }

    void testSave() {
        controller.save()

        assert model.subredditInstance != null
        assert view == '/subreddit/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/subreddit/show/1'
        assert controller.flash.message != null
        assert Subreddit.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/subreddit/list'

        populateValidParams(params)
        def subreddit = new Subreddit(params)

        assert subreddit.save() != null

        params.id = subreddit.id

        def model = controller.show()

        assert model.subredditInstance == subreddit
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/subreddit/list'

        populateValidParams(params)
        def subreddit = new Subreddit(params)

        assert subreddit.save() != null

        params.id = subreddit.id

        def model = controller.edit()

        assert model.subredditInstance == subreddit
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/subreddit/list'

        response.reset()

        populateValidParams(params)
        def subreddit = new Subreddit(params)

        assert subreddit.save() != null

        // test invalid parameters in update
        params.id = subreddit.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/subreddit/edit"
        assert model.subredditInstance != null

        subreddit.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/subreddit/show/$subreddit.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        subreddit.clearErrors()

        populateValidParams(params)
        params.id = subreddit.id
        params.version = -1
        controller.update()

        assert view == "/subreddit/edit"
        assert model.subredditInstance != null
        assert model.subredditInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/subreddit/list'

        response.reset()

        populateValidParams(params)
        def subreddit = new Subreddit(params)

        assert subreddit.save() != null
        assert Subreddit.count() == 1

        params.id = subreddit.id

        controller.delete()

        assert Subreddit.count() == 0
        assert Subreddit.get(subreddit.id) == null
        assert response.redirectedUrl == '/subreddit/list'
    }
}

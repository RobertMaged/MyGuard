package com.rmsr.myguard.domain.usecase


//@SmallTest
//@HiltAndroidTest
//class SearchCachingFacadeAndroidTest{
//
//        @get:Rule
//    val taskExecutorRule = InstantTaskExecutorRule()
//
//    @get:Rule
//    val hiltAndroidRule = HiltAndroidRule(this)
//
//    @get:Rule
//    val rxJavaSchedulersRule = RxJavaSchedulersRule()
//
//    @Inject
//    lateinit var database: MyGuardDatabase
//
//    private lateinit var jobRepo: JobRepo
//    private lateinit var relationsRepository: RelationsRepositoryImpl
//
//
//    @Before
//    fun setup(){
//        //   val context = ApplicationProvider.getApplicationContext<Context>()
//
////        database = Room
////            .inMemoryDatabaseBuilder(context, MyGuardDatabase::class.java)
////            .allowMainThreadQueries()
////            .addTypeConverter(BreachDbConverters())
////            .build()
////
//        hiltAndroidRule.inject()
//        val relationsDao = database.relationsDao()
//        val mapper = ScheduleCheckEntityMapperImpl()
//        val  scheduleCheckDao = database.scheduleCheckDao()
////
////
////
//        jobRepo = JobRepoImpl(scheduleCheckDao,
//            scheduleMapper = mapper,
//            relationsDao = relationsDao
//        )
//        relationsRepository = RelationsRepositoryImpl(relationsDao)
//    }
//
//    @After
//    fun closeAll(){
//        database.close()
//    }
//
//    @Test
//    fun cacheSearchedQueryByCacheSearchedQuery_SuccessfullyInsert() {
//
//        val cacheFacade = SearchCachingFacade(jobRepo, relationsRepository)
//
//        val search = "test"
//        val breaches = List(1){
//            Breach(name = "Test #$it")
//        }
//
//        val scheduleCheck = ScheduleCheck(id = 1, scheduledQuery = search)
//
//        cacheFacade.cacheSearchedQuery(search, breaches)
//            .test()
//            .assertNoErrors()
//            .assertComplete()
//
//        val scheduleWithBreaches = ScheduleCheckWithBreaches(
//            scheduleCheck.toEntity(),
//            breaches = breaches.map { it.toEntity() }.toList()
//        )
//
//        val cachedEmails = jobRepo.getAllCachedEmails()
//            .test()
//            .assertNoErrors()
//            .assertValueCount(1)
//            .values()
//            .first()
//
//        scheduleWithBreaches.scheduleCheck.createdTimeStamp = cachedEmails.first().createdTimeStamp
//
//        val size = relationsRepository.getScheduleCheckBreachCrossRefByQuery(1)
//            .test()
//            .values()
//            .first()
//            .size
//
//            assertThat(size).isEqualTo(1)
//
//
//        relationsRepository.getScheduleCheckWithBreaches(1)
//            .test()
//            .assertNoErrors()
//            .assertValueCount(1)
//           // .assertResult(scheduleWithBreaches)
//
//        assertThat(cachedEmails.first().id).isEqualTo(scheduleCheck.id)
//        assertThat(cachedEmails.first().scheduledQuery).isEqualTo(scheduleCheck.scheduledQuery)
//    }
//
//
//    @Test
//    fun crossReffTest(){
//
//    }
//}
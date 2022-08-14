package com.rmsr.myguard.data.repository

//import android.content.Context
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import com.rmsr.myguard.R
//import com.rmsr.myguard.data.database.dao.BreachDao
//import com.rmsr.myguard.data.database.entity.BreachEntity
//import com.rmsr.myguard.data.mapper.ReversibleMapper
//import com.rmsr.myguard.di.FakeRepositoryQualifier
//import com.rmsr.myguard.data.network.BreachesApiService
//import com.rmsr.myguard.data.network.PasswordsApiService
//import com.rmsr.myguard.data.network.pojo_response.BreachResponse
//import com.rmsr.myguard.di.BreachEntityMapper
//import com.rmsr.myguard.domain.repository.OldBreachRepository
//import com.rmsr.myguard.utils.*
//import io.reactivex.rxjava3.core.*
//import java.io.InputStreamReader
//import javax.inject.Inject
//
@Deprecated("")
class OldFakeRepository
//@FakeRepositoryQualifier
////@ViewModelScoped
//class OldFakeRepository @Inject constructor(
//    private val breachesApiService: dagger.Lazy<BreachesApiService>,
//    private val passwordsApiService: PasswordsApiService,
//    private val breachDao: BreachDao,
//    @BreachEntityMapper private val breachMapper: ReversibleMapper<BreachEntity, Breach>,
//    private val context: Context,
//) : OldBreachRepository() {
//
//    private val fakeBreachesApi = mutableListOf<BreachResponse>()
//    private val fakeBreachesDao = mutableListOf<BreachEntity>()
//    private val fakeBreachesDao2 = mutableMapOf<String, BreachEntity>()
//
//    override fun refreshSystemData(): Completable {
//        return getAllBreachesFromApi()
//            .flatMapCompletable { allBreaches ->
//                insertBreaches(allBreaches)
//            }
//    }
///*
////    @Inject
////    lateinit var newVersionDB: NewVersionDB
////
////    init {
////        GlobalScope.launch(Dispatchers.IO) {
////            delay(3000)
////            with(newVersionDB) {
////                //add breaches in its table
////                val breaches = List(566){it + 1L}
//////                getAllBreachesFromApi()
//////                    .map { breachMapper.mapReversed(it) }
//////                    .flatMap {
//////
//////                        breachDao.insert(breaches = it).map {
//////                            breaches = it
//////                        }
//////                    }.subscribe()
////
////                val queries = List(5) {
////                    QueryEntity(
////                        content = "mohamed${it+1}@gmail.com",
////                        type = QueryType.EMAIL
////                    )
////                }
////                val historiesId = mutableListOf<Long>()
////                val schedulesId = mutableListOf<Long>()
////                //add queries and add schedules and histories based on inserted queries ids.
////                Observable.fromIterable(queries)
////                    .concatMapSingle { queryDao().insertOrGet(queryEntity = it) }
////                    .timestamp()
////                    .concatMapSingle {
////                        historyDao().insert(
////                            history = SearchHistoryEntity(
////                                queryId = it.value(),
////                                createdTime = it.time(),
////                                lastAccessTime = it.time(),
////                                accessCount = 1
////                            )
////                        ).zipWith(
////                            scheduleDao().insert(
////                                ScheduleEntity(queryId = it.value(), createdTime = it.time())
////                            )
////                        ) { historyId, scheduleId ->
////                            historiesId.add(historyId)
////                            schedulesId.add(scheduleId)
////                        }
////                    }
////                    .subscribe()
////
////                //add ref between histories and breaches
////                Observable.fromIterable(historiesId).concatMapCompletable { hisId ->
////                    val hisref = breaches
////                        .subList(Random.nextInt(0, 250), Random.nextInt(250, 566))
////                        .map {
////                            HistoryBreachXRef(historyId = hisId, breachId = it)
////                        }
////                    historyBreachDao().insertHistoryBreachRef(hisref)
////                }.subscribe()
////
////                //add scan sessions and ref them with schedules, then simulate background check with records.
////                Observable.range(0, 5)
////                    .timestamp()
////                    .map { ScanSessionEntity(createdTime = it.time(), userRespond = false) }
////
////                    .concatMapSingle {
////                        scanSessionDao().insert(it)
////                    }
////                    .concatMapCompletable { insertedSessionId ->
////                        Completable.concatArray(
////                            Observable.fromIterable(schedulesId)
////                                .concatMapCompletable { schdid ->
////                                    sessionScheduleDao().insertSessionScheduleRef(
////                                        sessionId = insertedSessionId,
////                                        scheduleId = schdid
////                                    )
////                                },
////                            Observable.fromIterable(schedulesId)
////                                .concatMapCompletable { schdId ->
////                                    val records = breaches
////                                        .subList(Random.nextInt(0, 250), Random.nextInt(250, 566))
////                                        .map {
////                                            ScheduleScanRecordEntity(
////                                                scheduleId = schdId,
////                                                breachId = it,
////                                                sessionId = insertedSessionId,
////                                                userNotified = true,
////                                                notifyTime = System.currentTimeMillis(),
////                                                userAcknowledged = false,
////                                                acknowledgeTime = System.currentTimeMillis()
////                                            )
////                                        }
////                                    scanRecordDao().insert(scanRecords = records)
////                                }
////                        )
////
////                    }.subscribe()
////
////            }
////
////        }
////    }
//
// */
//
//    override fun getBreachesByPhoneTruncated(phoneNumber: String): Observable<List<Breach>> {
//        if (!isValidPhoneNumber(phoneNumber))
//            return Observable.error { InvalidPhoneNumberException() }
//
//        return breachesApiService.get()
//            .getBreachesByAccountOrPhone(query = phoneNumber, truncate = true)
//            .map { breachPojoList -> breachPojoList.map { it.toBreach() } }
//    }
//
//    override fun getBreachesByAccountTruncated(account: String): Observable<List<Breach>> {
//        if (!isValidEmail(account))
//            return Observable.error { InvalidEmailException() }
//
//        val dummyList = listOf<BreachResponse>(
//            BreachResponse(name = "Adobe"), BreachResponse(name = "Facebook"),
//            BreachResponse(name = "000webhost")
//        )
//        return Observable.just(dummyList)
////        return breachesApiService.get()
////            .getBreachesByAccountOrPhone(accountOrPhone = account, truncate = true)
//            .map { breachPojoList -> breachPojoList.map { it.toBreach() } }
//    }
//
//    override fun findEmailBreaches(email: String): Observable<List<Breach>> {
//        if (!isValidEmail(email))
//            return Observable.error { InvalidEmailException() }
//
//        return getBreachesByAccountTruncated(email)
//            .doAfterNext { MyLog.d(logThreadName = true, TAG, "BreachesTruncated: $it") }
//            .map { list -> list.map { it.name } }
//            .flatMap { breachesNames -> getBreachesByNameDB(breachesNames) }
//            .doAfterNext { MyLog.d(logThreadName = true, TAG, "BreachesComplete: $it") }
//
//    }
//
//    override fun findPhoneBreaches(phoneNumber: String): Observable<List<Breach>> {
//        if (!isValidPhoneNumber(phoneNumber))
//            return Observable.error { InvalidPhoneNumberException() }
//
//        return breachesApiService.get()
//            .getBreachesByAccountOrPhone(query = phoneNumber, truncate = true)
//            .doAfterNext { MyLog.d(logThreadName = true, TAG, "BreachesTruncated: $it") }
//            .map { list -> list.map { it.id } }
//            .flatMap { breachesNames -> getBreachesByNameDB(breachesNames) }
//            .doAfterNext { MyLog.d(logThreadName = true, TAG, "BreachesComplete: $it") }
//
//    }
//
//    override fun findPasswordBreachesCount(passwordHash: String): Observable<String> {
//        if (passwordHash.length > 5)
//            return Observable.error { InvalidPasswordHashException() }
//
//        return passwordsApiService.getAllHashes(passwordHash)
//    }
//
//    override fun getBreachByDomain(domain: String): Single<List<Breach>> {
//        if (!isValidDomain(domain))
//            return Single.error { InvalidDomainException() }
//
//        return breachesApiService.get().getBreachByDomain(domain)
//            .map { breachPojoList -> breachPojoList.map { it.toBreach() } }
//    }
//
//    override fun getBreachByName(name: String): Single<Breach> {
//        if (!isValidDomainName(name))
//            return Single.error { InvalidDomainNameException() }
//
//        return breachesApiService.get().getBreachByName(name)
//            .map { breachPojo -> breachPojo.toBreach() }
//    }
//
//    override fun getAllBreachesFromApi(): Single<List<Breach>> {
//
//        val jsonFile = context.resources.openRawResource(R.raw.data)
//
//        return Single.just(jsonFile)
//            .map { file ->
//                InputStreamReader(file).run {
//                    val json = readText()
//                    close()
//                    json
//                }
//            }
//            .map { json ->
//                Gson().fromJson<List<BreachResponse>>(
//                    json,
//                    object : TypeToken<List<BreachResponse>>() {}.type
//                )
//            }
//            .map { breachPojoList -> breachPojoList.map { it.toBreach() } }
////             .map { list ->
////                 list.map {breach -> breach.cachedTimeStamp = System.currentTimeMillis(); breach }
////             }
//
//    }
//
//    override fun insertBreaches(list: List<Breach>): Completable {
//        return Single.just(list)
//            .map { breaches -> breachMapper.mapReversed(breaches) }
//            .map { breachesEntities ->
//                breachesEntities.map { it.copy(createdTime = System.currentTimeMillis()) }
//            }
//            .flatMapCompletable { breachEntities ->
////                val news = breachEntities.associateBy { it.name }
////                fakeBreachesDao2.putAll(news)
////                Completable.complete()
//                breachDao.insertBreaches(breachEntities)
//            }
//            .doOnSubscribe { MyLog.d(logThreadName = true, TAG, "insertBreaches() -> Subscribed") }
//    }
//
//    override fun findDomainNameBreaches(breachName: String): Maybe<Breach> {
////        val breach = breachName//.lowercase().replaceFirstChar { it.uppercase(Locale.getDefault()) }
//        if (!isValidDomainName(breachName))
//            return Maybe.error { InvalidDomainNameException() }
//
//        return breachDao.getBreachByNameDB(breachName)
//            .map { breachEntity -> breachMapper.map(breachEntity) }
//    }
//
//    override fun getBreachesByNameDB(breachesNames: List<String>): Observable<List<Breach>> {
//        return breachDao.getBreachesByNameDB(breachesNames)
//            .map { breachEntities -> breachMapper.map(breachEntities) }
//
//    }
//
//    override fun findDomainBreaches(domain: String): Maybe<List<Breach>> {
//        if (!isValidDomain(domain))
//            return Maybe.error { InvalidDomainException() }
//
//        return breachDao.searchBreachByDomain(domain)//.lowercase())
//            .map { breachEntities -> breachMapper.map(breachEntities) }
//    }
//
//    override fun getAllBreachesDB(): Maybe<List<Breach>> {
//        return breachDao.allBreaches()
//            .map { breachEntities -> breachMapper.map(breachEntities) }
//    }
//
//    override fun deleteBreachDB(breachName: String): Completable {
//        return breachDao.delete(breachName)
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////////////
//
//
//    companion object {
//        private const val TAG = "Rob_FakeRepository"
//
//    }
//}
//
////    init {
////        getAllBreaches()
////            .flatMap { allBreaches -> insertBreaches(allBreaches).toSingle { } }
////            .subscribe (
////                { Log.d(TAG, "Save Done ") },
////                { error -> Log.d(TAG, "Error: ${error.message} ") }
////            )
////
////    }
//
////    init {
////        val email = "mohamed@yahoo.com"
////            Thread.sleep(2000)
////        getBreachesByAccountTruncated(email)
////            .doAfterNext { MyLog.d(logThreadName = true, TAG, "BreachesTruncated: $it") }
////            .map { list -> list.map { it.name } }
////            .flatMap { breachesNames -> getBreachesFromCache(breachesNames) }
////            .toList()
////            .subscribe(
////                { succes -> MyLog.d(logThreadName = true, TAG, "Save Done: $succes ") },
////                { error -> MyLog.d(logThreadName = true, TAG, "Error: ${error.message} ") })
////    }

package com.rmsr.myguard.domain.usecase

//import com.rmsr.myguard.domain.model.ScheduleCheck
//import com.rmsr.myguard.utils.logPrint
//import io.reactivex.rxjava3.core.*
//import org.junit.Assert.*
//import org.junit.Test
//
//class OldSchedulesCrudUseCaseTest {
//
//    @Test
//    fun cacheSearchedQuery(){
//        val insertEmail = Completable.complete().doOnComplete { logPrint(TAG, "insertEmail done") }
//            .doOnSubscribe {  logPrint(TAG, "insertEmail subscribed") }
//        val insertScheduleCheckWithBreaches = Completable.complete().doOnComplete { logPrint(TAG, "insertScheduleCheck done") }
//            .doOnSubscribe {  logPrint(TAG, "insertScheduleCheck subscribed") }
//
//
//         Maybe.empty<ScheduleCheck>().switchIfEmpty(
//            Maybe.just(1)
//                .map {
//                    ScheduleCheck(
//                        scheduledQuery = "query",
//                        createdTimeStamp = System.currentTimeMillis()
//                    )
//                }
//                .flatMapSingle { check ->
//                    insertEmail.toSingleDefault(check) }
//         )
//            .flatMapCompletable { check ->
//              //  val checkWithBreaches = ScheduleCheckWithBreaches(check.toEntity(), list.map { it.toEntity() })
//
//                insertScheduleCheckWithBreaches
//            }
//             .test()
//             .assertNoErrors()
//             .assertNoValues()
//             .assertComplete()
//
//
//    }
//
//
////    @Test
//    fun addScheduleQuery() {
//        val complete = Completable.complete().doOnComplete { logPrint(TAG, "completable done") }
//            .doOnSubscribe {  logPrint(TAG, "completable subscribed") }
//        val complete2 = Completable.complete().doOnComplete { logPrint(TAG, "completable2 done") }
//            .doOnSubscribe {  logPrint(TAG, "completable2 subscribed") }
//
//        val checkEmailByQuery = Maybe.just(1)
//
//           checkEmailByQuery
////               .switchIfEmpty (
////
////                   Maybe.just(1).flatMap {
////                       logPrint(TAG, "see what: completed")
////                       complete.toMaybe() } )
////
////               .flatMapCompletable {
////                   logPrint(TAG, "see what: $it")
////                   complete2
////               }
//
//               .flatMap(
//                {onFound ->
//                    logPrint(TAG, "see what: $onFound")
//
//                    return@flatMap Maybe.fromCompletable<Completable> ( complete)
//                },
//                {error ->
//                    logPrint(TAG, "addScheduleQuery() -> error: ${error.message}")
//                    return@flatMap Maybe.fromCompletable ( Completable.error(error) )
//                },
//                {
//                    logPrint(TAG, "see what: completed")
//                    return@flatMap Maybe.fromCompletable ( complete2 )
//                }
//            )
//
//               .subscribe()
//
//
//    }
//
//    companion object{
//        private const val TAG = "Rob_Test"
//    }
//}
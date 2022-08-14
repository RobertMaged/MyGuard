package com.rmsr.myguard.data.mapper

class ScheduleEntityMapperTest
//{
//    private val scheduleMapper = OldScheduleCheckEntityMapperImpl()
//    private lateinit var scheduleEntity: OldScheduleCheckEntity
//    private lateinit var schedule: _Schedule
//
//    @Before
//    fun setup() {
//        scheduleEntity = OldScheduleCheckEntity(scheduledQuery = "Test")
//
//        schedule = _Schedule(query = "Test")
//    }
//
//    private fun OldScheduleCheckEntity.toScheduleCheckForTest() = _Schedule(
//        id = this.id,
//        query = this.scheduledQuery,
//        queryType = this.queryType,
//        isMuted = this.isScheduled,
//        unSeenBreaches = this.unseenBreachesCount,
//        createdTime = this.createdTimeStamp,
//        lastCheckTimeStamp = this.lastCheckTimeStamp,
//        foundNewLeaks = this.foundNewLeaks,
//        lastSeenUserTimeStamp = this.lastSeenUserTimeStamp
//    )
//
//
//    @Test
//    fun `map valid ScheduleCheckEntity, return new Schedule`() {
//        val mappedSchedule = scheduleMapper.map(scheduleEntity)
//
//        assertThat(mappedSchedule).isEqualTo(scheduleEntity.toScheduleCheckForTest())
//    }
//
//    @Test
//    fun `map valid ScheduleCheckEntity, source should not changed, return new Schedule`() {
//        val oldScheduleCheckEntity = scheduleEntity.copy()
//        val mappedSchedule = scheduleMapper.map(scheduleEntity)
//
//        assertThat(mappedSchedule).isEqualTo(scheduleEntity.toScheduleCheckForTest())
//        assertThat(oldScheduleCheckEntity).isEqualTo(scheduleEntity)
//    }
//
//    @Test
//    fun `map valid ScheduleCheckEntityList, return new ScheduleList`() {
//        val entityList = List(5){
//            scheduleEntity.copy(scheduledQuery = "Test $it")
//        }
//        val mappedScheduleList = scheduleMapper.map(entityList)
//
//        assertThat(mappedScheduleList).isEqualTo(entityList.map { it.toScheduleCheckForTest() })
//    }
//
//    @Test
//    fun `map valid ScheduleCheckEntityList, source should not changed, return new ScheduleList`() {
//        val entityList = List(5){
//            scheduleEntity.copy(scheduledQuery = "Test $it")
//        }
//        val oldEntityList = entityList.map { it.copy() }
//        val mappedScheduleList = scheduleMapper.map(entityList)
//
//        assertThat(mappedScheduleList).isEqualTo(entityList.map { it.toScheduleCheckForTest() })
//        assertThat(oldEntityList).isEqualTo(entityList)
//    }
//
//
//    @Test
//    fun `mapReversed() map valid Schedule, return new ScheduleCheckEntity`() {
//        val mappedScheduleCheckEntity = scheduleMapper.mapReversed(schedule)
//
//        assertThat(mappedScheduleCheckEntity.toScheduleCheckForTest()).isEqualTo(schedule)
//    }
//
//    @Test
//    fun `mapReversed() map valid Schedule, source should not changed, return new ScheduleCheckEntity`() {
//        val oldSchedule = schedule.copy()
//        val mappedScheduleCheckEntity = scheduleMapper.mapReversed(schedule)
//
//        assertThat(mappedScheduleCheckEntity.toScheduleCheckForTest()).isEqualTo(schedule)
//        assertThat(oldSchedule).isEqualTo(schedule)
//    }
//
//    @Test
//    fun `mapReversed() map valid ScheduleList, return new ScheduleCheckEntityList`() {
//        val scheduleList = List(5){
//            schedule.copy(query = "Test $it")
//        }
//
//        val mappedScheduleList = scheduleMapper.mapReversed(scheduleList)
//
//        assertThat(scheduleList).isEqualTo(mappedScheduleList.map { it.toScheduleCheckForTest() })
//    }
//
//    @Test
//    fun `mapReversed() map valid ScheduleList, source should not changed, return new ScheduleCheckEntityList`() {
//        val scheduleList = List(5){
//            schedule.copy(query = "Test $it")
//        }
//
//        val oldScheduleList = scheduleList.map { it.copy() }
//
//        val mappedScheduleList = scheduleMapper.mapReversed(scheduleList)
//
//        assertThat(scheduleList).isEqualTo(mappedScheduleList.map { it.toScheduleCheckForTest() })
//        assertThat(scheduleList).isEqualTo(oldScheduleList)
//    }
//}
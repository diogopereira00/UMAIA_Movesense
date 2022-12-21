import com.umaia.movesense.data.suveys.questions.Question
import com.umaia.movesense.data.suveys.questions.QuestionDao
import com.umaia.movesense.data.suveys.questions_types.QuestionTypes
import com.umaia.movesense.data.suveys.questions_types.QuestionTypesDao

class QuestionTypesRepository(private val questionTypesDao: QuestionTypesDao) {

    suspend fun add(questionTypes: QuestionTypes){
        questionTypesDao.addTypes(questionTypes)
    }
}
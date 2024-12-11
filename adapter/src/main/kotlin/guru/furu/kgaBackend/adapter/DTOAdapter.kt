package guru.furu.kgaBackend.adapter

import guru.furu.kgaBackend.client.dto.AccountDTO
import guru.furu.kgaBackend.client.dto.NewAccountDTO
import guru.furu.kgaBackend.client.dto.NewCommentDTO
import guru.furu.kgaBackend.client.dto.NewImageDTO
import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.incoming.NewAccount
import guru.furu.kgaBackend.domain.nodes.incoming.NewComment
import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import java.util.UUID

fun NewAccountDTO.toDomain(): NewAccount =
    NewAccount(
        userName = this.userName,
        email = this.email,
        password = this.password,
    )

fun Account.toAccountDTO(): AccountDTO =
    AccountDTO(
        id = this.id.toString(),
        userName = this.userName,
        email = this.email,
        lastLogin = this.lastLogin,
    )

fun NewImageDTO.toDomain(): NewImage =
    NewImage(
        uploaderAccountId = UUID.fromString(this.uploaderAccountId),
        title = this.title,
        description = this.description,
    )

fun NewCommentDTO.toDomain(): NewComment =
    NewComment(
        authorId = UUID.fromString(this.authorId),
        onImageId = UUID.fromString(onImageId),
        text = this.text,
    )
